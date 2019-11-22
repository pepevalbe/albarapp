package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.persistence.domain.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class InvoiceDocument {

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

	public static void generate(Invoice invoice, OutputStream outputStream) {

		List<DeliveryNote> deliveryNotes = invoice.getDeliveryNotes();

		if (deliveryNotes.size() == 0 || deliveryNotes.size() > MAX_ROW_NUMBER) {
			log.error("Invoice is too large or empty");
			throw new ApiException(ApiError.ApiError009);
		}

		Customer customer = deliveryNotes.get(0).getCustomer();
		PDDocument pdfDocument = null;
		try {
			pdfDocument = PDDocument.load(new File(TEMPLATE_PDF_DOCUMENT));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError009);
		}
		PDAcroForm pdfForm = pdfDocument.getDocumentCatalog().getAcroForm();

		setDocumentHeader(pdfForm, customer, invoice);

		double grossTotal = 0;
		double vatTotal = 0;
		int rowCounter = 0;
		for (DeliveryNote deliveryNote : deliveryNotes) {
			for (DeliveryNoteItem deliveryNoteItem : deliveryNote.getDeliveryNoteItems()) {
				double partialGross = deliveryNoteItem.getQuantity() * deliveryNoteItem.getPrice();
				grossTotal += partialGross;
				vatTotal += partialGross * deliveryNoteItem.getProduct().getTax() * 0.01f;
				setDocumentRow(pdfForm, rowCounter, deliveryNoteItem);
				rowCounter++;
			}
		}

		setDocumentFooter(pdfForm, grossTotal, vatTotal);

		pdfForm.getFields().forEach(pdField -> pdField.setReadOnly(true));    // Make form fields not editable

		try {
			pdfDocument.save(outputStream);
			pdfDocument.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private static void setDocumentHeader(PDAcroForm form, Customer customer, Invoice invoice) {
		try {
			form.getField(CUSTOMER_FIELD).setValue(customer.getName());
			form.getField(CUSTOMER_CODE_FIELD).setValue(String.valueOf(customer.getCode()));
			form.getField(CUSTOMER_ADDRESS_FIELD).setValue(customer.getAddress());
			form.getField(CUSTOMER_FISCAL_FIELD).setValue(customer.getFiscalId());
			form.getField(CUSTOMER_PROVINCE_FIELD).setValue(customer.getProvince());
			form.getField(INVOICE_ID_FIELD).setValue("F" + invoice.getId());
			form.getField(DATE_FIELD).setValue(DATE_FORMAT.format(new Date(invoice.getIssuedTimestamp())));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Error filling invoice header");
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private static void setDocumentRow(PDAcroForm form, int row, DeliveryNoteItem deliveryNoteItem) {
		try {
			form.getField(String.format(ROW_DATE_FIELD, row)).setValue(DATE_FORMAT.format(new Date(deliveryNoteItem.getDeliveryNote().getIssuedTimestamp())));
			form.getField(String.format(ROW_QUANTITY_FIELD, row)).setValue(String.valueOf(deliveryNoteItem.getQuantity()));
			form.getField(String.format(ROW_PRODUCT_FIELD, row)).setValue(deliveryNoteItem.getProduct().getName());
			form.getField(String.format(ROW_ORDER_FIELD, row)).setValue(deliveryNoteItem.getDeliveryNote().getAuxDeliveryNoteNr());
			form.getField(String.format(ROW_VAT_FIELD, row)).setValue(String.format("%,.1f", deliveryNoteItem.getProduct().getTax()) + " %");
			form.getField(String.format(ROW_PRICE_FIELD, row)).setValue(String.format("%,.2f", deliveryNoteItem.getPrice()) + " €");
			form.getField(String.format(ROW_TOTAL_FIELD, row)).setValue(String.format("%,.2f", deliveryNoteItem.getQuantity() * deliveryNoteItem.getPrice()) + " €");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Error filling invoice row " + row);
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private static void setDocumentFooter(PDAcroForm form, double grossTotal, double vatTotal) {
		try {
			form.getField(GROSS_FIELD).setValue(String.format("%,.2f", grossTotal) + " €");
			form.getField(DISCOUNT_FIELD).setValue(String.format("%d", 0) + " %");
			form.getField(VAT_FIELD).setValue(String.format("%,.2f", vatTotal) + " €");
			form.getField(AMOUNT_FIELD).setValue(String.format("%,.2f", grossTotal + vatTotal) + " €");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Error filling footer");
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private InvoiceDocument() {
	}
}