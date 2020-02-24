package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.InvoiceService;
import com.pepe.albarapp.service.dto.DeliveryNoteDto;
import com.pepe.albarapp.service.dto.InvoiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
public class InvoiceController {

	private static final String DELIVERY_NOTES_ENDPOINT = "/api/deliveryNotes";
	private static final String INVOICE_ENDPOINT = "/api/invoices";
	private static final String INVOICE_BILL_ENDPOINT = "/api/invoices/bill";

	@Autowired
	private InvoiceService invoiceService;

	@GetMapping(DELIVERY_NOTES_ENDPOINT)
	public ResponseEntity<Page<DeliveryNoteDto>> getDeliveryNotes(@RequestParam @Nullable Integer customerCode,
																  @RequestParam @Nullable Long timestampFrom,
																  @RequestParam @Nullable Long timestampTo,
																  @PageableDefault(sort = "id") Pageable pageable) {

		return ResponseEntity.ok(invoiceService.getDeliveryNotes(customerCode, timestampFrom, timestampTo, pageable));
	}

	@PostMapping(DELIVERY_NOTES_ENDPOINT)
	public ResponseEntity<DeliveryNoteDto> postDeliveryNote(@RequestBody DeliveryNoteDto deliveryNoteDto) {

		return ResponseEntity.ok(invoiceService.persistDeliveryNote(deliveryNoteDto));
	}

	@DeleteMapping(DELIVERY_NOTES_ENDPOINT)
	public ResponseEntity<Void> deleteDeliveryNote(@RequestBody DeliveryNoteDto deliveryNoteDto) {

		return invoiceService.deleteDeliveryNote(deliveryNoteDto) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@GetMapping(INVOICE_ENDPOINT)
	public ResponseEntity<Page<InvoiceDto>> getInvoices(@RequestParam @Nullable Integer customerCode,
														@RequestParam @Nullable Long timestampFrom,
														@RequestParam @Nullable Long timestampTo,
														@RequestParam @Nullable List<Integer> productCodes,
														@PageableDefault(sort = "id") Pageable pageable) {

		return ResponseEntity.ok(invoiceService.getInvoices(customerCode, timestampFrom, timestampTo, productCodes, pageable));
	}

	@PostMapping(INVOICE_BILL_ENDPOINT)
	public ResponseEntity<List<InvoiceDto>> billProcess(@RequestParam Integer customerCodeFrom,
														@RequestParam Integer customerCodeTo,
														@RequestParam Long timestampFrom,
														@RequestParam Long timestampTo,
														@RequestParam Long issuedTimestamp) {

		return ResponseEntity.ok(invoiceService.billProcess(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo, issuedTimestamp));
	}
}
