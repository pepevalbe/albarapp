package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.api.log.ApiLog;
import com.pepe.albarapp.persistence.domain.*;
import com.pepe.albarapp.persistence.repository.*;
import com.pepe.albarapp.service.dto.DeliveryNoteDto;
import com.pepe.albarapp.service.dto.InvoiceDto;
import com.pepe.albarapp.service.mapping.InvoiceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceFilterRepository invoiceFilterRepository;

	@Autowired
	private DeliveryNoteRepository deliveryNoteRepository;

	@Autowired
	private DeliveryNoteItemRepository deliveryNoteItemRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InvoiceMapper invoiceMapper;

	@Transactional(readOnly = true)
	public Page<DeliveryNoteDto> getDeliveryNotes(Integer customerCode, Long timestampFrom, Long timestampTo, Pageable pageable) {
		return deliveryNoteRepository
				.filterByCustomerCodeAndTimestampRange(customerCode, timestampFrom, timestampTo, pageable)
				.map(invoiceMapper::map);
	}

	@Transactional(readOnly = true)
	public Page<DeliveryNoteDto> getDeliveryNotesByProducts(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes, Pageable pageable) {
		return deliveryNoteRepository
				.findByCustomerAndTimestampRangeAndProducts(customerCode, timestampFrom, timestampTo, productCodes, pageable)
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
			Set<DeliveryNoteItem> deliveryNoteItems = deliveryNoteItemRepository.findByDeliveryNote(deliveryNote.get());
			deliveryNoteItemRepository.deleteAll(deliveryNoteItems);
			deliveryNoteRepository.delete(deliveryNote.get());
			return true;
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Page<InvoiceDto> getInvoices(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes, Pageable pageable) {

		return invoiceFilterRepository.filter(customerCode, timestampFrom, timestampTo, productCodes, pageable).map(invoiceMapper::map);
	}

	@Transactional(readOnly = true)
	public Page<InvoiceDto> getInvoicesInterval(Long idFrom, Long idTo, Pageable pageable) {

		return invoiceRepository.findByIdBetween(idFrom, idTo, pageable).map(invoiceMapper::map);
	}

	@Transactional
	public List<InvoiceDto> billProcess(Integer customerCodeFrom, Integer customerCodeTo, Long timestampFrom,
										Long timestampTo, Long issuedTimestamp) {

		// Find delivery notes for the requested customerCode and timestamp range
		Set<DeliveryNote> deliveryNotesToBill = deliveryNoteRepository.
				findByCustomerCodeBetweenAndIssuedTimestampBetweenAndInvoiceIsNull(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo);

		// Group delivery notes by customer
		Map<Customer, List<DeliveryNote>> deliveryNotesByCustomer = deliveryNotesToBill.stream()
				.collect(Collectors.groupingBy(DeliveryNote::getCustomer));

		ApiLog.updateElapsedTime();
		log.info(String.format("%s delivery notes retrieved grouped in to %s customers", deliveryNotesToBill.size(), deliveryNotesByCustomer.entrySet().size()));

		// Create invoice for each customer, ordered by ascending customer code
		List<Invoice> createdInvoices = deliveryNotesByCustomer.keySet().stream()
				.sorted(Comparator.comparingInt(Customer::getCode))
				.map(customer -> {
					Invoice invoice = new Invoice();
					invoice.setIssuedTimestamp(issuedTimestamp);
					invoice.setCustomer(customer);
					invoice.setDeliveryNotes(new HashSet<>(deliveryNotesByCustomer.get(customer)));
					Invoice createdInvoice = invoiceRepository.save(invoice);
					deliveryNotesByCustomer.get(customer).forEach(deliveryNote -> deliveryNote.setInvoice(createdInvoice));
					return createdInvoice;
				})
				.collect(Collectors.toList());

		// Update all delivery notes
		deliveryNoteRepository.saveAll(deliveryNotesByCustomer.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));

		ApiLog.updateElapsedTime();
		log.info(String.format("%s invoices created", createdInvoices.size()));

		return createdInvoices.stream().map(invoiceMapper::map).collect(Collectors.toList());
	}
}