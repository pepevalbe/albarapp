package com.pepe.albarapp.controller;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

	private final static String CUSTOMERS_ENDPOINT = "/api/customers";
	private final static String DELIVERY_NOTES_ENDPOINT = "/api/deliveryNotes";

	@Autowired
	private CustomerService customerService;

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
}
