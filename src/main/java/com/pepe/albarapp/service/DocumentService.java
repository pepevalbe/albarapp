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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class DocumentService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	public void generateInvoice(long invoiceId, ServletOutputStream outputStream) {
		Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException(ApiError.ApiError009));

		InvoiceDocument.generate(invoice, outputStream);
	}

	public void generateMultipleInvoices(List<Long> invoiceIds, ServletOutputStream outputStream) throws IOException {

		ZipOutputStream zipOut = new ZipOutputStream(outputStream);

		for (Long invoiceId : invoiceIds) {
			Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException(ApiError.ApiError009));

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

}
