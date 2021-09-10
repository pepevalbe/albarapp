package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.CustomerService;
import com.pepe.albarapp.service.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
public class CustomerController {

	private static final String CUSTOMERS_ENDPOINT = "/api/customers";
	private static final String CUSTOMERS_CHECK_CODE_ENDPOINT = "/api/customers/check-code";

	@Autowired
	private CustomerService customerService;

	@GetMapping(CUSTOMERS_ENDPOINT + "/{id}")
	public ResponseEntity<CustomerDto> getCustomer(@PathVariable String id) {
		return ResponseEntity.ok(customerService.getCustomer(id));
	}

	@GetMapping(CUSTOMERS_ENDPOINT)
	public ResponseEntity<List<CustomerDto>> getCustomers(@RequestParam @Nullable String alias) {

		if (alias != null) {
			ResponseEntity.ok(customerService.findCustomersByAlias(alias));
		}
		return ResponseEntity.ok(customerService.getAllCustomers());
	}

	@GetMapping(CUSTOMERS_CHECK_CODE_ENDPOINT)
	public ResponseEntity<Boolean> isCustomerCodeExisting(@RequestParam Integer code) {

		return ResponseEntity.ok(customerService.isExistingCustomerCode(code));
	}

	@PostMapping(CUSTOMERS_ENDPOINT)
	public ResponseEntity<CustomerDto> postCustomer(@RequestBody CustomerDto customerDto) {

		customerDto.setId(null);
		return ResponseEntity.ok(customerService.createCustomer(customerDto));
	}

	@PutMapping(CUSTOMERS_ENDPOINT + "/{id}")
	public ResponseEntity<CustomerDto> putCustomer(@PathVariable String id, @RequestBody CustomerDto customerDto) {

		customerDto.setId(id);
		return ResponseEntity.ok(customerService.updateCustomer(customerDto));
	}
}
