package com.pepe.albarapp.service.document;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.persistence.repository.InvoiceFilterRepository;
import com.pepe.albarapp.persistence.repository.InvoiceRepository;
import com.pepe.albarapp.service.document.aecoc.AecocInvoice;
import com.pepe.albarapp.service.document.csv.CsvFile;
import com.pepe.albarapp.service.document.pdf.PdfInvoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DocumentService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceFilterRepository invoiceFilterRepository;

	public AecocInvoice generateAecocInvoice(long invoiceId) {

		return invoiceRepository.findById(invoiceId).map(AecocInvoice::build).orElseThrow(() -> new ApiException(ApiError.ApiError011));
	}

	public void generatePdfInvoice(long invoiceId, ServletOutputStream outputStream) {
		Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException(ApiError.ApiError009));

		PdfInvoice.generate(invoice, outputStream);
	}

	public void generateMultiplePdfInvoices(List<Long> invoiceIds, ServletOutputStream outputStream) throws IOException {

		ZipOutputStream zipOut = new ZipOutputStream(outputStream);

		for (Long invoiceId : invoiceIds) {
			Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException(ApiError.ApiError009));

			ByteArrayOutputStream invoiceStream = new ByteArrayOutputStream();
			PdfInvoice.generate(invoice, invoiceStream);
			ByteArrayInputStream fis = new ByteArrayInputStream(invoiceStream.toByteArray());
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

	public CsvFile generateCsvFile(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes) {

		List<Invoice> invoices = invoiceFilterRepository.filter(customerCode, timestampFrom, timestampTo, productCodes);

		return new CsvFile(invoices);
	}
}
