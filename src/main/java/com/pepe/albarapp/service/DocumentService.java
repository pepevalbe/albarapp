package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
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
import java.util.List;

@Slf4j
@Service
public class DocumentService {

	private static final String TEMPLATE_PDF_DOCUMENT = "template.pdf";
	private static final String DOCUMENT_FIELD_NAME = "";

	@Autowired
	private InvoiceRepository invoiceRepository;

	public void generateInvoice(Long invoiceId, ServletOutputStream outputStream) {
		Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException(ApiError.ApiError001));

		List<DeliveryNote> deliveryNotes = invoice.getDeliveryNotes();

		if (deliveryNotes.size() == 0 || deliveryNotes.size() > 30) {
			throw new ApiException(ApiError.ApiError001);
		}

		try {
			PDDocument pdfDocument = PDDocument.load(new File(TEMPLATE_PDF_DOCUMENT));
			PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			if (acroForm != null) {
				for (PDField field : acroForm.getFields()) {
					System.out.println(field.getFullyQualifiedName());
				}

				PDField field = acroForm.getFields().get(0);
				field.setValue(deliveryNotes.get(0).getCustomer().getName());
			}
			pdfDocument.save(outputStream);
			pdfDocument.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}
}
