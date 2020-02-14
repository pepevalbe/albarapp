package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RestController
public class DocumentController {

	private static final String INVOICE_DOWNLOAD_ENDPOINT = "/api/invoices/download";
	private static final String INVOICE_DOWNLOAD_MULTIPLE_ENDPOINT = "/api/invoices/download/multiple";

	@Autowired
	private DocumentService documentService;

	@GetMapping(INVOICE_DOWNLOAD_ENDPOINT)
	public void downloadInvoice(@RequestParam Long invoiceId, HttpServletResponse response) {
		try {
			documentService.generateInvoice(invoiceId, response.getOutputStream());
			response.setContentType("application/pdf; filename=Invoice.pdf");
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}

	@GetMapping(INVOICE_DOWNLOAD_MULTIPLE_ENDPOINT)
	public void downloadMultipleInvoice(@RequestParam Long[] invoiceId, HttpServletResponse response) {
		try {
			documentService.generateMultipleInvoices(Arrays.asList(invoiceId), response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=invoices.zip");
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}
}
