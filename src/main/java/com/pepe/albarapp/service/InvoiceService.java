package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.api.log.ApiLog;
import com.pepe.albarapp.persistence.domain.*;
import com.pepe.albarapp.persistence.repository.DeliveryNoteItemRepository;
import com.pepe.albarapp.persistence.repository.DeliveryNoteRepository;
import com.pepe.albarapp.persistence.repository.InvoiceRepository;
import com.pepe.albarapp.persistence.repository.ProductRepository;
import com.pepe.albarapp.service.dto.DeliveryNoteDto;
import com.pepe.albarapp.service.dto.InvoiceDto;
import com.pepe.albarapp.service.mapping.InvoiceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private DeliveryNoteRepository deliveryNoteRepository;

	@Autowired
	private DeliveryNoteItemRepository deliveryNoteItemRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InvoiceMapper invoiceMapper;

	@Transactional(readOnly = true)
	public Page<DeliveryNoteDto> getDeliveryNotes(@RequestParam Integer customerCode, @RequestParam Long timestampFrom,
												  @RequestParam Long timestampTo, Pageable pageable) {
		return deliveryNoteRepository
				.filterByCustomerCodeAndTimestampRange(customerCode, timestampFrom, timestampTo, pageable)
				.map(invoiceMapper::map);
	}

	@Transactional
	public DeliveryNoteDto persistDeliveryNote(DeliveryNoteDto deliveryNoteDto) {

		// Get products id list for further check
		Set<String> productIds = productRepository.findAll().stream().map(Product::getId).collect(Collectors.toSet());

		// Check delivery note items relations
		deliveryNoteDto.getDeliveryNoteItems().forEach(deliveryNoteItem -> {
			if (!productIds.contains(deliveryNoteItem.getProductId())) {
				throw new ApiException(ApiError.ApiError006);
			}
		});

		// Create delivery note
		DeliveryNote createdDeliveryNote = deliveryNoteRepository.save(invoiceMapper.map(deliveryNoteDto));

		// Create delivery note items
		List<DeliveryNoteItem> deliveryNoteItems = deliveryNoteDto.getDeliveryNoteItems().stream()
				.map(deliveryNoteItemDto -> {
					DeliveryNoteItem deliveryNoteItem = invoiceMapper.map(deliveryNoteItemDto);
					deliveryNoteItem.setDeliveryNote(createdDeliveryNote);
					return deliveryNoteItem;
				}).collect(Collectors.toList());

		deliveryNoteItemRepository.saveAll(deliveryNoteItems);
		log.info("DeliveryNote created/updated: " + createdDeliveryNote.getId());
		return invoiceMapper.map(createdDeliveryNote);
	}

	@Transactional
	public boolean deleteDeliveryNote(DeliveryNoteDto deliveryNoteDto) {

		Optional<DeliveryNote> deliveryNote = deliveryNoteRepository.findById(deliveryNoteDto.getId());
		if (deliveryNote.isPresent()) {
			List<DeliveryNoteItem> deliveryNoteItems = deliveryNoteItemRepository.findByDeliveryNote(deliveryNote.get());
			deliveryNoteItemRepository.deleteAll(deliveryNoteItems);
			deliveryNoteRepository.delete(deliveryNote.get());
			return true;
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Page<InvoiceDto> getInvoices(@RequestParam Integer customerCode, @RequestParam Long timestampFrom,
										@RequestParam Long timestampTo, @RequestParam List<Integer> productCodes, Pageable pageable) {

		if (productCodes != null && !productCodes.isEmpty()) {
			return invoiceRepository.filterByCustomerCodeAndTimestampRangeAndProducts(customerCode, timestampFrom, timestampTo, productCodes, pageable);
		} else {
			return invoiceRepository.filterByCustomerCodeAndTimestampRange(customerCode, timestampFrom, timestampTo, pageable);
		}
	}

	@Transactional
	public List<InvoiceDto> billProcess(Integer customerCodeFrom, Integer customerCodeTo, Long timestampFrom,
										Long timestampTo, Long issuedTimestamp) {

		// Find delivery notes for the requested customerCode and timestamp range
		List<DeliveryNote> deliveryNotesToBill = deliveryNoteRepository.
				findByCustomerCodeBetweenAndIssuedTimestampBetweenAndInvoiceIsNull(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo);

		// Group delivery notes by customer
		Map<Customer, List<DeliveryNote>> deliveryNotesByCustomer = deliveryNotesToBill.stream()
				.collect(Collectors.groupingBy(DeliveryNote::getCustomer));

		// Create invoice for each customer, ordered by ascending customer code
		List<Invoice> createdInvoices = deliveryNotesByCustomer.keySet().stream()
				.sorted(Comparator.comparingInt(Customer::getCode))
				.map(customer -> {
					Invoice invoice = new Invoice();
					invoice.setIssuedTimestamp(issuedTimestamp);
					invoice.setCustomer(customer);
					invoice.setDeliveryNotes(deliveryNotesByCustomer.get(customer));
					Invoice createdInvoice = invoiceRepository.save(invoice);
					deliveryNotesByCustomer.get(customer).forEach(deliveryNote -> deliveryNote.setInvoice(createdInvoice));
					return createdInvoice;
				})
				.collect(Collectors.toList());

		// Update all delivery notes
		deliveryNoteRepository.saveAll(deliveryNotesByCustomer.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));

		ApiLog.updateElapsedTime();
		log.info(String.format("%s invoices created from %s delivery notes: ", createdInvoices.size(), deliveryNotesByCustomer.values().size()));
		return createdInvoices.stream().map(invoiceMapper::map).collect(Collectors.toList());
	}
}