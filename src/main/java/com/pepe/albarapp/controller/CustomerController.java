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
import java.util.List;

@Slf4j
@RestController
public class CustomerController {

	private final static String CUSTOMERS_ENDPOINT = "/api/customers";
	private final static String DELIVERY_NOTES_ENDPOINT = "/api/deliveryNotes";
	private final static String PDF_ENDPOINT = "/api/pdf";

	@Autowired
	private CustomerService customerService;

	@Autowired
	private DocumentService documentService;

	@PostMapping(CUSTOMERS_ENDPOINT)
	public ResponseEntity postCustomer(@RequestBody Customer customer) {

		customer.setId(null);
		Customer persistedCustomer = customerService.persistCustomer(customer);

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

	@PostMapping(PDF_ENDPOINT)
	public void generateInvoiceDoc(@RequestParam Long invoiceId, HttpServletResponse response) {

		try {
			documentService.generateInvoice(invoiceId, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ApiException(ApiError.ApiError001);
		}
	}
}
