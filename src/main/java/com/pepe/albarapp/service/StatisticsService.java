package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.repository.*;
import com.pepe.albarapp.service.dto.StatisticsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class StatisticsService {

	private static final String TOTAL_CUSTOMERS = "Total clientes";
	private static final String TOTAL_INVOICES = "Total facturas";
	private static final String TOTAL_DELIVERY_NOTES = "Total albaranes";

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private DeliveryNoteRepository deliveryNoteRepository;

	public List<StatisticsDto> getStatistics() {

		List<StatisticsDto> statistics = new ArrayList<>();
		statistics.add(new StatisticsDto(TOTAL_CUSTOMERS, String.valueOf(customerRepository.count())));
		statistics.add(new StatisticsDto(TOTAL_INVOICES, String.valueOf(invoiceRepository.count())));
		statistics.add(new StatisticsDto(TOTAL_DELIVERY_NOTES, String.valueOf(deliveryNoteRepository.count())));

		return statistics;
	}
}