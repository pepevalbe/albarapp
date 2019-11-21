package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.service.CustomerService;
import com.pepe.albarapp.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class CustomerController {

	private final static String CUSTOMERS_ENDPOINT = "/api/customers";
	private final static String DELIVERY_NOTES_ENDPOINT = "/api/deliveryNotes";
	private final static String INVOICE_DOWNLOAD_ENDPOINT = "/api/invoice/download";

	@Autowired
	private CustomerService customerService;

	@Autowired
	private DocumentService documentService;

	@GetMapping(CUSTOMERS_ENDPOINT)
	public ResponseEntity getCustomers() {
		return ResponseEntity.ok(customerService.getAllCustomers());
	}

	@PostMapping(CUSTOMERS_ENDPOINT)
	public ResponseEntity postCustomer(@RequestBody Customer customer) {

		customer.setId(null);
		customerService.persistCustomer(customer);

		return ResponseEntity.ok().build();
	}

	@PutMapping(CUSTOMERS_ENDPOINT + "/{id}")
	public ResponseEntity putCustomer(@PathVariable String id, @RequestBody Customer customer) {

		customer.setId(id);
		customerService.persistCustomer(customer);

		return ResponseEntity.ok().build();
	}

	@PostMapping(DELIVERY_NOTES_ENDPOINT)
	public ResponseEntity postDeliveryNote(@RequestBody DeliveryNote deliveryNote) {

		deliveryNote.setId(null);
		customerService.persistDeliveryNote(deliveryNote);

		return ResponseEntity.ok().build();
	}

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
}
