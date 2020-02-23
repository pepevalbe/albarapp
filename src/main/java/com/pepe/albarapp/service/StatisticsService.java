package com.pepe.albarapp.service;

import com.pepe.albarapp.service.dto.StatisticsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StatisticsService {

	private static final String TOTAL_CUSTOMERS = "Total clientes";
	private static final String TOTAL_CUSTOMERS_QUERY = "select count(ID) from customer";
	private static final String TOTAL_INVOICES = "Total facturas";
	private static final String TOTAL_INVOICES_QUERY = "select count(ID) from invoice";
	private static final String TOTAL_DELIVERY_NOTES = "Total albaranes";
	private static final String TOTAL_DELIVERY_NOTES_QUERY = "select count(ID) from delivery_note";
	private static final String MOST_BILLED_CUSTOMER = "Cliente mayor facturación";
	private static final String MOST_BILLED_CUSTOMER_QUERY = "select alias, SUM(quantity*price) as total FROM delivery_note dn INNER JOIN customer cus ON dn.customer_id = cus.id INNER JOIN delivery_note_item dni ON dni.delivery_note_id = dn.id GROUP BY cus.id ORDER BY total DESC LIMIT 1";

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<StatisticsDto> getStatistics() {

		List<StatisticsDto> statistics = new ArrayList<>();

		statistics.add(new StatisticsDto(TOTAL_CUSTOMERS, jdbcTemplate.queryForObject(TOTAL_CUSTOMERS_QUERY, String.class)));
		statistics.add(new StatisticsDto(TOTAL_INVOICES, jdbcTemplate.queryForObject(TOTAL_INVOICES_QUERY, String.class)));
		statistics.add(new StatisticsDto(TOTAL_DELIVERY_NOTES, jdbcTemplate.queryForObject(TOTAL_DELIVERY_NOTES_QUERY, String.class)));
		List<Object> mostBilledResult = new ArrayList<>(jdbcTemplate.queryForMap(MOST_BILLED_CUSTOMER_QUERY).values());
		statistics.add(new StatisticsDto(MOST_BILLED_CUSTOMER, String.format("%s (%s€)", mostBilledResult.get(0), mostBilledResult.get(1))));

		return statistics;
	}
}