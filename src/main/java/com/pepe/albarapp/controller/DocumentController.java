package com.pepe.albarapp.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.document.DocumentService;
import com.pepe.albarapp.service.document.aecoc.AecocInvoice;
import com.pepe.albarapp.service.document.csv.CsvFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Log
@RestController
public class DocumentController {

	private static final String INVOICES_AECOC_ENDPOINT = "/api/invoices/download/edi";
	private static final String INVOICES_PDF_ENDPOINT = "/api/invoices/download/pdf";
	private static final String INVOICES_PDF_MULTIPLE_ENDPOINT = "/api/invoices/download/pdf/multiple";
	private static final String INVOICES_CSV_ENDPOINT = "/api/invoices/download/csv";

	private static final XmlMapper XML_MAPPER = new XmlMapper();

	static {
		XML_MAPPER.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
	}

	@Autowired
	private DocumentService documentService;

	@GetMapping(INVOICES_AECOC_ENDPOINT)
	@ResponseBody
	public void downloadAecocInvoice(@RequestParam Long invoiceId, HttpServletResponse response) {

		try {
			AecocInvoice aecocInvoice = documentService.generateAecocInvoice(invoiceId);
			XML_MAPPER.writeValue(response.getOutputStream(), aecocInvoice);
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=invoice.xml");
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}

	@GetMapping(INVOICES_PDF_ENDPOINT)
	public void downloadPdfInvoice(@RequestParam Long invoiceId, HttpServletResponse response) {
		try {
			documentService.generatePdfInvoice(invoiceId, response.getOutputStream());
			response.setContentType("application/pdf; filename=Invoice.pdf");
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}

	@GetMapping(INVOICES_PDF_MULTIPLE_ENDPOINT)
	public void downloadMultiplePdfInvoices(@RequestParam Long[] invoiceId, HttpServletResponse response) {
		try {
			documentService.generateMultiplePdfInvoices(Arrays.asList(invoiceId), response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=invoices.zip");
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}

	@GetMapping(INVOICES_CSV_ENDPOINT)
	public void generateCsvInvoices(@RequestParam @Nullable Integer customerCode,
									@RequestParam @Nullable Long timestampFrom,
									@RequestParam @Nullable Long timestampTo,
									@RequestParam @Nullable List<Integer> productCodes,
									HttpServletResponse response) {

		try {
			CsvFile csvFile = documentService.generateCsvFile(customerCode, timestampFrom, timestampTo, productCodes);
			csvFile.write(response.getWriter());
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=invoices.csv");
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}
}
