package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.persistence.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Service
public class DocumentService {

	private static final String TEMPLATE_PDF_DOCUMENT = "template.pdf";
	private static final String CUSTOMER_FIELD = "customer";
	private static final String CUSTOMER_CODE_FIELD = "customerCode";
	private static final String CUSTOMER_ADDRESS_FIELD = "customerAddress";
	private static final String CUSTOMER_FISCAL_FIELD = "customerFiscalId";
	private static final String CUSTOMER_PROVINCE_FIELD = "customerProvince";
	private static final String ROW_DATE_FIELD = "rowDate";
	private static final String ROW_QUANTITY_FIELD = "rowQuantity";
	private static final String ROW_PRODUCT_FIELD = "rowProduct";
	private static final String ROW_ORDER_FIELD = "rowOrder";
	private static final String ROW_VAT_FIELD = "rowVat";
	private static final String ROW_PRICE_FIELD = "rowPrice";
	private static final String ROW_TOTAL_FIELD = "rowTotal";
	private static final String INVOICE_ID_FIELD = "invoiceId";
	private static final String DATE_FIELD = "date";
	private static final String GROSS_FIELD = "grossTotal";
	private static final String DISCOUNT_FIELD = "discount";
	private static final String VAT_0_FIELD = "vat0Total";
	private static final String VAT_3_FIELD = "vat3Total";
	private static final String AMOUNT_FIELD = "amount";

	@Autowired
	private InvoiceRepository invoiceRepository;

	public void generateInvoice(Long invoiceId, ServletOutputStream outputStream) {
		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ApiException(ApiError.ApiError009));

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		List<DeliveryNote> deliveryNotes = invoice.getDeliveryNotes();

		if (deliveryNotes.size() == 0 || deliveryNotes.size() > 23) {
			throw new ApiException(ApiError.ApiError009);
		}

		Customer customer = deliveryNotes.get(0).getCustomer();
		Double amount = deliveryNotes.stream().flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream())
				.map(deliveryNoteItem -> deliveryNoteItem.getPrice() * deliveryNoteItem.getQuantity())
				.mapToDouble(value -> value).sum();

		try {
			PDDocument pdfDocument = PDDocument.load(new File(TEMPLATE_PDF_DOCUMENT));
			PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			if (acroForm != null) {

				acroForm.getField(CUSTOMER_FIELD).setValue(customer.getName());
				acroForm.getField(CUSTOMER_CODE_FIELD).setValue(String.valueOf(customer.getCode()));
				acroForm.getField(CUSTOMER_ADDRESS_FIELD).setValue(customer.getAddress());
				acroForm.getField(CUSTOMER_FISCAL_FIELD).setValue(customer.getFiscalId());
				acroForm.getField(CUSTOMER_PROVINCE_FIELD).setValue(customer.getProvince());
				acroForm.getField(INVOICE_ID_FIELD).setValue("F" + String.valueOf(invoiceId));
				acroForm.getField(DATE_FIELD).setValue(dateFormat.format(new Date(invoice.getIssuedTimestamp())));

				int rowCounter = 0;
				double grossTotal = 0;
				double netTotal = 0;
				double vat3Total = 0;
				for (DeliveryNote deliveryNote : deliveryNotes) {
					for (DeliveryNoteItem deliveryNoteItem : deliveryNote.getDeliveryNoteItems()) {

						double partialGross = deliveryNoteItem.getQuantity() * deliveryNoteItem.getPrice();
						grossTotal += partialGross;
						if (deliveryNoteItem.getProduct().getTax() == 3f) {
							vat3Total += 0.03 * partialGross;
						}

						acroForm.getField(ROW_DATE_FIELD + "." + String.valueOf(rowCounter))
								.setValue(dateFormat.format(new Date(deliveryNote.getIssuedTimestamp())));
						acroForm.getField(ROW_QUANTITY_FIELD + "." + String.valueOf(rowCounter))
								.setValue(String.valueOf(deliveryNoteItem.getQuantity()));
						acroForm.getField(ROW_PRODUCT_FIELD + "." + String.valueOf(rowCounter))
								.setValue(deliveryNoteItem.getProduct().getName());
						acroForm.getField(ROW_ORDER_FIELD + "." + String.valueOf(rowCounter))
								.setValue(deliveryNote.getAuxDeliveryNoteNr());
						acroForm.getField(ROW_VAT_FIELD + "." + String.valueOf(rowCounter))
								.setValue(String.format("%,.1f", deliveryNoteItem.getProduct().getTax()) + " %");
						acroForm.getField(ROW_PRICE_FIELD + "." + String.valueOf(rowCounter))
								.setValue(String.format("%,.2f", deliveryNoteItem.getPrice()) + " €");
						acroForm.getField(ROW_TOTAL_FIELD + "." + String.valueOf(rowCounter))
								.setValue(String.format("%,.2f", partialGross) + " €");
						rowCounter++;
					}
				}

				acroForm.getField(GROSS_FIELD).setValue(String.format("%,.2f", grossTotal) + " €");
				acroForm.getField(DISCOUNT_FIELD).setValue(String.format("%d", 0) + " %");
				acroForm.getField(VAT_0_FIELD).setValue(String.format("%,.2f", 0f) + " €");
				acroForm.getField(VAT_3_FIELD).setValue(String.format("%,.2f", vat3Total) + " €");
				acroForm.getField(AMOUNT_FIELD).setValue(String.format("%,.2f", grossTotal+vat3Total) + " €");

				// Make form fields not editable
				for (PDField field : acroForm.getFields()) {
					field.setReadOnly(true);
				}

			}
			pdfDocument.save(outputStream);
			pdfDocument.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}
}
