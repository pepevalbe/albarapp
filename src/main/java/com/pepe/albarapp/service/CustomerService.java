package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.CustomerProductPrice;
import com.pepe.albarapp.persistence.domain.Product;
import com.pepe.albarapp.persistence.repository.CustomerProductPriceRepository;
import com.pepe.albarapp.persistence.repository.CustomerRepository;
import com.pepe.albarapp.persistence.repository.ProductRepository;
import com.pepe.albarapp.service.dto.CustomerDto;
import com.pepe.albarapp.service.mapping.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerProductPriceRepository customerProductPriceRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CustomerMapper customerMapper;

	@Transactional(readOnly = true)
	public List<CustomerDto> getAllCustomers() {

		return customerRepository.findAll().stream().map(customerMapper::map).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<CustomerDto> findCustomersByAlias(String alias) {

		return customerRepository.findByAliasContaining(alias).stream().map(customerMapper::map).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public boolean isExistingCustomerCode(int code) {

		return customerRepository.findByCode(code).isPresent();
	}

	@Transactional
	public CustomerDto createCustomer(CustomerDto customerDto) {

		if (customerRepository.findByCode(customerDto.getCode()).isPresent()) {
			throw new ApiException(ApiError.ApiError010);
		}

		// Get products id list for further check
		Set<String> productIds = productRepository.findAll().stream().map(Product::getId).collect(Collectors.toSet());

		// Check customer product prices relations
		customerDto.getCustomerProductPrices().forEach(customerProductPriceDto -> {
			if (!productIds.contains(customerProductPriceDto.getProductId())) {
				throw new ApiException(ApiError.ApiError006);
			}
		});

		// Create customer
		Customer createdCustomer = customerRepository.save(customerMapper.map(customerDto));

		// Create customer product prices
		List<CustomerProductPrice> customerProductPrices = customerDto.getCustomerProductPrices().stream()
				.map(customerProductPriceDto -> {
					CustomerProductPrice customerProductPrice = customerMapper.map(customerProductPriceDto);
					customerProductPrice.setCustomer(createdCustomer);
					return customerProductPrice;
				}).collect(Collectors.toList());

		customerProductPriceRepository.saveAll(customerProductPrices);

		log.info("Customer created: " + createdCustomer.getId());
		return customerMapper.map(createdCustomer);
	}

	@Transactional
	public CustomerDto updateCustomer(CustomerDto customerDto) {

		customerRepository.findById(customerDto.getId()).orElseThrow(() -> new ApiException(ApiError.ApiError006));

		// Get products id list for further check
		Set<String> productIds = productRepository.findAll().stream().map(Product::getId).collect(Collectors.toSet());

		// Check customer product prices relations
		customerDto.getCustomerProductPrices().forEach(customerProductPriceDto -> {
			if (!productIds.contains(customerProductPriceDto.getProductId())) {
				throw new ApiException(ApiError.ApiError006);
			}
		});

		// Delete all previous customer product price
		customerProductPriceRepository.deleteByCustomerId(customerDto.getId());

		// Update customer
		Customer persistedCustomer = customerRepository.save(customerMapper.map(customerDto));

		// Create customer product prices
		List<CustomerProductPrice> customerProductPrices = customerDto.getCustomerProductPrices().stream()
				.map(customerProductPriceDto -> {
					CustomerProductPrice customerProductPrice = customerMapper.map(customerProductPriceDto);
					customerProductPrice.setCustomer(persistedCustomer);
					return customerProductPrice;
				}).collect(Collectors.toList());

		customerProductPriceRepository.saveAll(customerProductPrices);

		log.info("Customer updated: " + persistedCustomer.getId());
		return customerMapper.map(persistedCustomer);
	}
}
