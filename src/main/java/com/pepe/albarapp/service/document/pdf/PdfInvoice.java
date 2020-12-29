package com.pepe.albarapp.service.document.pdf;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.persistence.domain.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PdfInvoice {

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
	private static final String VAT_FIELD = "vatTotal";
	private static final String AMOUNT_FIELD = "amount";
	private static final String PAGE_NUMBER_FIELD = "pageNumber";
	private static final String PAGE_TOTAL_FIELD = "pageTotal";
	private static final int MAX_ROW_NUMBER = 29;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(new Locale("es", "ES"));
	private static final NumberFormat VAT_FORMAT = NumberFormat.getInstance(new Locale("es", "ES"));

	public static void generate(Invoice invoice, OutputStream outputStream) {

		// Used for price and totals
		NUMBER_FORMAT.setMinimumFractionDigits(2);

		// Get delivery notes ordered by date then by id
		List<DeliveryNote> deliveryNotes = invoice.getDeliveryNotes().stream()
				.sorted(Comparator.comparing(DeliveryNote::getIssuedTimestamp).thenComparing(DeliveryNote::getId))
				.collect(Collectors.toList());

		if (deliveryNotes.isEmpty()) {
			log.error("Invoice is empty");
			throw new ApiException(ApiError.ApiError009);
		}

		Customer customer = deliveryNotes.get(0).getCustomer();
		List<PDDocument> pdfDocumentList = new ArrayList<PDDocument>();
		PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
		PDAcroForm pdfForm = addNewPage(pdfDocumentList);

		BigDecimal grossTotal = new BigDecimal(0);
		BigDecimal vatTotal = new BigDecimal(0);
		int rowCounter = 0;
		for (DeliveryNote deliveryNote : deliveryNotes) {
			// Get delivery notes sorted by productCode
			List<DeliveryNoteItem> deliveryNoteItems = deliveryNote.getDeliveryNoteItems().stream()
					.sorted(Comparator.comparing(d -> d.getProduct().getCode())).collect(Collectors.toList());

			for (DeliveryNoteItem deliveryNoteItem : deliveryNoteItems) {

				if (rowCounter >= MAX_ROW_NUMBER) {
					rowCounter = 0;
					pdfForm = addNewPage(pdfDocumentList);
				}

				BigDecimal quantity = new BigDecimal(deliveryNoteItem.getQuantity());
				BigDecimal price = new BigDecimal(Double.toString(deliveryNoteItem.getPrice()));
				BigDecimal partialGross = quantity.multiply(price);
				grossTotal = grossTotal.add(partialGross);
				BigDecimal taxProduct = new BigDecimal(Double.toString(deliveryNoteItem.getProduct().getTax()));
				BigDecimal vatRow = taxProduct.multiply(new BigDecimal("0.01"));
				BigDecimal partialVat = partialGross.multiply(vatRow);
				vatTotal = vatTotal.add(partialVat);
				setDocumentRow(pdfForm, rowCounter, deliveryNoteItem);
				rowCounter++;
			}
		}

		// Set header, footer and pagination for all pages
		for (int i = 0; i < pdfDocumentList.size(); i++) {
			PDAcroForm pdfFormIterator = pdfDocumentList.get(i).getDocumentCatalog().getAcroForm();
			setDocumentHeader(pdfFormIterator, customer, invoice);
			setDocumentFooter(pdfFormIterator, grossTotal, vatTotal);
			setPagintationFooter(pdfFormIterator, i + 1, pdfDocumentList.size());
			// Make form fields not editable
			pdfFormIterator.getFieldIterator().forEachRemaining(pdField -> pdField.setReadOnly(true));
		}

		try {
			// Take first document as root, then append and close the rest
			PDDocument pdfDocument = pdfDocumentList.get(0);
			for (int i = 1; i < pdfDocumentList.size(); i++) {
				pdfMergerUtility.appendDocument(pdfDocument, pdfDocumentList.get(i));
				pdfDocumentList.get(i).close();
			}
			pdfDocument.save(outputStream);
			pdfDocument.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private static PDAcroForm addNewPage(List<PDDocument> pdfDocumentList) {
		PDDocument pdfDocument;
		try {
			pdfDocument = PDDocument.load(new File(TEMPLATE_PDF_DOCUMENT));
			pdfDocumentList.add(pdfDocument);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError009);
		}
		return pdfDocument.getDocumentCatalog().getAcroForm();
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
			BigDecimal quantity = new BigDecimal(deliveryNoteItem.getQuantity());
			BigDecimal vat = new BigDecimal(deliveryNoteItem.getProduct().getTax());
			BigDecimal price = new BigDecimal(Double.toString(deliveryNoteItem.getPrice()));
			BigDecimal rowTotal = quantity.multiply(price);
			rowTotal = rowTotal.setScale(2, RoundingMode.HALF_UP);
			form.getField(String.format(ROW_DATE_FIELD, row))
					.setValue(DATE_FORMAT.format(new Date(deliveryNoteItem.getDeliveryNote().getIssuedTimestamp())));
			form.getField(String.format(ROW_QUANTITY_FIELD, row))
					.setValue(String.valueOf(deliveryNoteItem.getQuantity()));
			form.getField(String.format(ROW_PRODUCT_FIELD, row)).setValue(deliveryNoteItem.getProduct().getName());
			form.getField(String.format(ROW_ORDER_FIELD, row))
					.setValue(deliveryNoteItem.getDeliveryNote().getAuxDeliveryNoteNr());
			form.getField(String.format(ROW_VAT_FIELD, row)).setValue(VAT_FORMAT.format(vat) + " %");
			form.getField(String.format(ROW_PRICE_FIELD, row)).setValue(NUMBER_FORMAT.format(price) + " €");
			form.getField(String.format(ROW_TOTAL_FIELD, row)).setValue(NUMBER_FORMAT.format(rowTotal) + " €");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Error filling invoice row " + row);
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private static void setDocumentFooter(PDAcroForm form, BigDecimal grossTotal, BigDecimal vatTotal) {
		try {
			BigDecimal netTotal = grossTotal.add(vatTotal);
			vatTotal = vatTotal.setScale(2, RoundingMode.HALF_UP);
			netTotal = netTotal.setScale(2, RoundingMode.HALF_UP);
			grossTotal = grossTotal.setScale(2, RoundingMode.HALF_UP);
			form.getField(GROSS_FIELD).setValue(NUMBER_FORMAT.format(grossTotal) + " €");
			form.getField(VAT_FIELD).setValue(NUMBER_FORMAT.format(vatTotal) + " €");
			form.getField(AMOUNT_FIELD).setValue(NUMBER_FORMAT.format(netTotal) + " €");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Error filling footer");
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private static void setPagintationFooter(PDAcroForm form, int pageNum, int pageTotal) {
		try {
			form.getField(PAGE_NUMBER_FIELD).setValue(String.valueOf(pageNum));
			form.getField(PAGE_TOTAL_FIELD).setValue(String.valueOf(pageTotal));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Error filling footer");
			throw new ApiException(ApiError.ApiError009);
		}
	}

	private PdfInvoice() {
	}
}
