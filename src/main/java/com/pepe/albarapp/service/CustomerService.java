package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.domain.*;
import com.pepe.albarapp.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CustomerProductPriceRepository customerProductPriceRepository;

	@Autowired
	private DeliveryNoteRepository deliveryNoteRepository;

	@Autowired
	private DeliveryNoteItemRepository deliveryNoteItemRepository;

	@Transactional
	public Customer persistCustomer(Customer customer) {

		// Get products id list for further check
		Set<String> productIds = productRepository.findAll().stream().map(Product::getId).collect(Collectors.toSet());

		// Check customer product prices relations
		for (CustomerProductPrice customerProductPrice : customer.getCustomerProductPrices()) {
			if (!productIds.contains(customerProductPrice.getProduct().getId())) {
				return null;
			}
		}

		// Delete all customer product price if customer already exists
		if (customer.getId() != null) {
			customerProductPriceRepository.deleteByCustomerId(customer.getId());
		}

		// Create or update customer
		Customer persistedCustomer = customerRepository.save(customer);

		// Create customer product prices
		customer.getCustomerProductPrices().forEach(customerProductPrice -> customerProductPrice.setCustomer(persistedCustomer));
		customerProductPriceRepository.saveAll(customer.getCustomerProductPrices());

		return persistedCustomer;
	}

	@Transactional
	public DeliveryNote persistDeliveryNote(DeliveryNote deliveryNote) {

		// Get products id list for further check
		Set<String> productIds = productRepository.findAll().stream().map(Product::getId).collect(Collectors.toSet());

		// Check delivery note items relations
		for (DeliveryNoteItem deliveryNoteItem : deliveryNote.getDeliveryNoteItems()) {
			if (!productIds.contains(deliveryNoteItem.getProduct().getId())) {
				return null;
			}
		}

		// Create delivery note and items
		DeliveryNote createdDeliveryNote = deliveryNoteRepository.save(deliveryNote);
		deliveryNote.getDeliveryNoteItems().forEach(deliveryNoteItem -> deliveryNoteItem.setDeliveryNote(createdDeliveryNote));
		deliveryNoteItemRepository.saveAll(deliveryNote.getDeliveryNoteItems());

		return createdDeliveryNote;
	}

}
