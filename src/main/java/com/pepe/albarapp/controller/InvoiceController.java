package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.service.DocumentService;
import com.pepe.albarapp.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class InvoiceController {

	private final static String INVOICE_BILL_ENDPOINT = "/api/invoice/bill";
	private final static String INVOICE_DOWNLOAD_ENDPOINT = "/api/invoice/download";
	private final static String INVOICE_DOWNLOAD_MULTIPLE_ENDPOINT = "/api/invoice/download/multiple";

	@Autowired
	private DocumentService documentService;

	@Autowired
	private InvoiceService invoiceService;

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

	@PostMapping(INVOICE_BILL_ENDPOINT)
	public ResponseEntity<List<Invoice>> billProcess(@RequestParam Integer customerCodeFrom, @RequestParam Integer customerCodeTo, @RequestParam Long timestampFrom, @RequestParam Long timestampTo, @RequestParam Long issuedTimestamp) {

		return ResponseEntity.ok(invoiceService.billProcess(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo, issuedTimestamp));

	}

}
