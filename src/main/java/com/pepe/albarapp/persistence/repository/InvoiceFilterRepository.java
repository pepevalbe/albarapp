package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceFilterRepository {

	@Autowired
	InvoiceRepository invoiceRepository;

	public List<Invoice> filter(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes) {

		if (productCodes == null || productCodes.isEmpty()) {
			return invoiceRepository.findIdByCustomerCodeAndTimestampRange(customerCode, timestampFrom, timestampTo);
		} else {
			return invoiceRepository.findIdByCustomerCodeAndTimestampRangeAndProducts(customerCode, timestampFrom, timestampTo, productCodes);
		}
	}

	public Page<Invoice> filter(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes, Pageable pageable) {
		Page<Long> ids;

		if (productCodes == null || productCodes.isEmpty()) {
			ids = invoiceRepository.findIdByCustomerCodeAndTimestampRange(customerCode, timestampFrom, timestampTo, pageable).map(Invoice::getId);
		} else {
			ids = invoiceRepository.findIdByCustomerCodeAndTimestampRangeAndProducts(customerCode, timestampFrom, timestampTo, productCodes, pageable).map(Invoice::getId);
		}

		List<Invoice> invoices = invoiceRepository.findByIdIn(ids.getContent(), pageable.getSort());

		return new PageImpl<>(invoices, pageable, ids.getTotalElements());
	}
}
