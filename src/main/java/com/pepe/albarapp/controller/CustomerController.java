package com.pepe.albarapp.controller;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {

	private final static String CUSTOMERS_ENDPOINT = "/api/customers";
	private final static String DELIVERY_NOTES_ENDPOINT = "/api/deliveryNotes";

	@Autowired
	private CustomerService customerService;

	@GetMapping(CUSTOMERS_ENDPOINT)
	public ResponseEntity<List<Customer>> getCustomers() {
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

	@GetMapping(DELIVERY_NOTES_ENDPOINT)
	public ResponseEntity<Page<DeliveryNote>> getDeliveryNotes(@RequestParam @Nullable Integer customerCode,
															   @RequestParam @Nullable Long timestampFrom,
															   @RequestParam @Nullable Long timestampTo,
															   Pageable pageable) {

		System.out.println(pageable.getPageNumber());
		System.out.println(pageable.getPageSize());
		return ResponseEntity.ok(customerService.getDeliveryNotes(customerCode, timestampFrom, timestampTo, pageable));
	}

	@PostMapping(DELIVERY_NOTES_ENDPOINT)
	public ResponseEntity postDeliveryNote(@RequestBody DeliveryNote deliveryNote) {

		deliveryNote.setId(null);
		customerService.persistDeliveryNote(deliveryNote);

		return ResponseEntity.ok().build();
	}
}
