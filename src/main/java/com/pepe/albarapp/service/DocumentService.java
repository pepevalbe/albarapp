package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.persistence.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

		InvoiceDocument.generate(invoice, outputStream);
	}

	public void generateMultipleInvoices(List<Long> invoiceIds, ServletOutputStream outputStream) throws IOException {

		ZipOutputStream zipOut = new ZipOutputStream(outputStream);

		for (Long invoiceId : invoiceIds) {
			Invoice invoice = invoiceRepository.findById(invoiceId)
					.orElseThrow(() -> new ApiException(ApiError.ApiError009));

			File tempFile = new File("temp");
			InvoiceDocument.generate(invoice, new FileOutputStream(tempFile));
			FileInputStream fis = new FileInputStream(tempFile);
			ZipEntry zipEntry = new ZipEntry(invoiceId + ".pdf");
			zipOut.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fis.close();
		}
		zipOut.close();
	}

	public void example() throws IOException {
		List<String> srcFiles = Arrays.asList("test1.txt", "test2.txt");
		FileOutputStream fos = new FileOutputStream("multiCompressed.zip");

		ZipOutputStream zipOut = new ZipOutputStream(fos);

		for (String srcFile : srcFiles) {
			File fileToZip = new File(srcFile);
			FileInputStream fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
			zipOut.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fis.close();
		}
		zipOut.close();
		fos.close();
	}
}
