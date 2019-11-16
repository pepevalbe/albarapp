package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
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
import java.util.List;

@Slf4j
@Service
public class DocumentService {

	private static final String TEMPLATE_PDF_DOCUMENT = "template.pdf";
	private static final String CUSTOMER_FIELD = "customer";
	private static final String INVOICE_ID_FIELD = "invoiceId";
	private static final String DATE_FIELD = "date";
	private static final String AMOUNT_FIELD = "amount";

	@Autowired
	private InvoiceRepository invoiceRepository;

	public void generateInvoice(Long invoiceId, ServletOutputStream outputStream) {
		Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException(ApiError.ApiError009));

		List<DeliveryNote> deliveryNotes = invoice.getDeliveryNotes();

		if (deliveryNotes.size() == 0 || deliveryNotes.size() > 30) {
			throw new ApiException(ApiError.ApiError009);
		}

		Customer customer = deliveryNotes.get(0).getCustomer();
		double amount = deliveryNotes.stream()
				.flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream())
				.map(deliveryNoteItem -> deliveryNoteItem.getPrice() * deliveryNoteItem.getQuantity())
				.mapToDouble(value -> value).sum();

		try {
			PDDocument pdfDocument = PDDocument.load(new File(TEMPLATE_PDF_DOCUMENT));
			PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			if (acroForm != null) {
				for (PDField field : acroForm.getFields()) {
					System.out.println(field.getFullyQualifiedName());
				}

				acroForm.getField(CUSTOMER_FIELD).setValue(customer.getName());
				acroForm.getField(INVOICE_ID_FIELD).setValue(String.valueOf(invoiceId));
				acroForm.getField(DATE_FIELD).setValue(new Date(invoice.getIssuedTimestamp()).toString());
				acroForm.getField(AMOUNT_FIELD).setValue(amount + "€");

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
