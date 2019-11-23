package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.persistence.repository.DeliveryNoteRepository;
import com.pepe.albarapp.persistence.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private DeliveryNoteRepository deliveryNoteRepository;

	@Transactional
	public List<Invoice> billProcess(Integer customerCodeFrom, Integer customerCodeTo, Long timestampFrom, Long timestampTo, Long issuedTimestamp) {

		List<Invoice> createdInvoices = new ArrayList<>();

		// Find delivery notes for the requested customerCode and timestamp range
		List<DeliveryNote> deliveryNotesToBill = deliveryNoteRepository.findByCustomerCodeBetweenAndIssuedTimestampBetweenAndInvoiceIsNull(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo);

		// Get delivery notes by customer
		Map<Customer, List<DeliveryNote>> deliveryNotesByCustomer = deliveryNotesToBill.stream()
				.collect(Collectors.groupingBy(DeliveryNote::getCustomer));

		// Create invoice for each customer
		deliveryNotesByCustomer.forEach((customer, deliveryNotes) -> {
			Invoice invoice = new Invoice();
			invoice.setIssuedTimestamp(issuedTimestamp);
			Invoice createdInvoice = invoiceRepository.save(invoice);
			createdInvoices.add(createdInvoice);
			deliveryNotes.forEach(deliveryNote -> deliveryNote.setInvoice(createdInvoice));
		});

		// Update all delivery notes
		deliveryNoteRepository.saveAll(deliveryNotesByCustomer.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
		return createdInvoices;
	}
}