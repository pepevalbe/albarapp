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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private static final String ROW_DATE_FIELD = "rowDate.%d";
	private static final String ROW_QUANTITY_FIELD = "rowQuantity.%d";
	private static final String ROW_PRODUCT_FIELD = "rowProduct.%d";
	private static final String ROW_ORDER_FIELD = "rowOrder.%d";
	private static final String ROW_VAT_FIELD = "rowVat.%d";
	private static final String ROW_PRICE_FIELD = "rowPrice.%d";
	private static final String ROW_TOTAL_FIELD = "rowTotal.%d";
	private static final String INVOICE_ID_FIELD = "invoiceId";
	private static final String DATE_FIELD = "date";
	private static final String GROSS_FIELD = "grossTotal";
	private static final String DISCOUNT_FIELD = "discount";
	private static final String VAT_FIELD = "vatTotal";
	private static final String AMOUNT_FIELD = "amount";
	private static final int MAX_ROW_NUMBER = 23;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	@Autowired
	private InvoiceRepository invoiceRepository;

	public void generateInvoice(long invoiceId, ServletOutputStream outputStream) {
		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ApiException(ApiError.ApiError009));


		List<DeliveryNote> deliveryNotes = invoice.getDeliveryNotes();

		if (deliveryNotes.size() == 0 || deliveryNotes.size() > MAX_ROW_NUMBER) {
			throw new ApiException(ApiError.ApiError009);
		}

		Customer customer = deliveryNotes.get(0).getCustomer();

		try {
			PDDocument pdfDocument = PDDocument.load(new File(TEMPLATE_PDF_DOCUMENT));
			PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			setDocumentHeader(acroForm, customer, invoice);

			double grossTotal = 0;
			double vatTotal = 0;
			int rowCounter = 0;
			for (DeliveryNote deliveryNote : deliveryNotes) {
				for (DeliveryNoteItem deliveryNoteItem : deliveryNote.getDeliveryNoteItems()) {
					double partialGross = deliveryNoteItem.getQuantity() * deliveryNoteItem.getPrice();
					grossTotal += partialGross;
					vatTotal += partialGross * deliveryNoteItem.getProduct().getTax() * 0.01f;
					setDocumentRow(acroForm, rowCounter, deliveryNoteItem);
					rowCounter++;
				}
			}

			setDocumentFooter(acroForm, grossTotal, vatTotal);

			acroForm.getFields().forEach(pdField -> pdField.setReadOnly(true));    // Make form fields not editable
			pdfDocument.save(outputStream);
			pdfDocument.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}

	private void setDocumentHeader(PDAcroForm form, Customer customer, Invoice invoice) throws IOException {
		form.getField(CUSTOMER_FIELD).setValue(customer.getName());
		form.getField(CUSTOMER_CODE_FIELD).setValue(String.valueOf(customer.getCode()));
		form.getField(CUSTOMER_ADDRESS_FIELD).setValue(customer.getAddress());
		form.getField(CUSTOMER_FISCAL_FIELD).setValue(customer.getFiscalId());
		form.getField(CUSTOMER_PROVINCE_FIELD).setValue(customer.getProvince());
		form.getField(INVOICE_ID_FIELD).setValue("F" + invoice.getId());
		form.getField(DATE_FIELD).setValue(DATE_FORMAT.format(new Date(invoice.getIssuedTimestamp())));
	}

	private void setDocumentRow(PDAcroForm form, int row, DeliveryNoteItem deliveryNoteItem) throws IOException {
		form.getField(String.format(ROW_DATE_FIELD, row)).setValue(DATE_FORMAT.format(new Date(deliveryNoteItem.getDeliveryNote().getIssuedTimestamp())));
		form.getField(String.format(ROW_QUANTITY_FIELD, row)).setValue(String.valueOf(deliveryNoteItem.getQuantity()));
		form.getField(String.format(ROW_PRODUCT_FIELD, row)).setValue(deliveryNoteItem.getProduct().getName());
		form.getField(String.format(ROW_ORDER_FIELD, row)).setValue(deliveryNoteItem.getDeliveryNote().getAuxDeliveryNoteNr());
		form.getField(String.format(ROW_VAT_FIELD, row)).setValue(String.format("%,.1f", deliveryNoteItem.getProduct().getTax()) + " %");
		form.getField(String.format(ROW_PRICE_FIELD, row)).setValue(String.format("%,.2f", deliveryNoteItem.getPrice()) + " €");
		form.getField(String.format(ROW_TOTAL_FIELD, row)).setValue(String.format("%,.2f", deliveryNoteItem.getQuantity() * deliveryNoteItem.getPrice()) + " €");
	}

	private void setDocumentFooter(PDAcroForm form, double grossTotal, double vatTotal) throws IOException {
		form.getField(GROSS_FIELD).setValue(String.format("%,.2f", grossTotal) + " €");
		form.getField(DISCOUNT_FIELD).setValue(String.format("%d", 0) + " %");
		form.getField(VAT_FIELD).setValue(String.format("%,.2f", vatTotal) + " €");
		form.getField(AMOUNT_FIELD).setValue(String.format("%,.2f", grossTotal + vatTotal) + " €");
	}
}
