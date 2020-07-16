package com.pepe.albarapp.service;

import com.pepe.albarapp.service.dto.StatisticsDto;
import com.pepe.albarapp.service.dto.statistics.MonthlyEvolutionDto;
import com.pepe.albarapp.service.dto.statistics.RankingDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticsService {

	private static final String TOTAL_CUSTOMERS = "Total clientes";
	private static final String TOTAL_CUSTOMERS_QUERY = "select count(ID) from customer";
	private static final String TOTAL_INVOICES = "Total facturas";
	private static final String TOTAL_INVOICES_QUERY = "select count(ID) from invoice";
	private static final String TOTAL_DELIVERY_NOTES = "Total albaranes";
	private static final String TOTAL_DELIVERY_NOTES_QUERY = "select count(ID) from delivery_note";
	private static final String RANKING_CUSTOMER_QUERY = "select alias, SUM(dni.quantity*dni.price) as total FROM delivery_note dn INNER JOIN customer cus ON dn.customer_id = cus.id INNER JOIN delivery_note_item dni ON dni.delivery_note_id = dn.id GROUP BY cus.id ORDER BY total DESC LIMIT 10";
	private static final String MONTHLY_EVOLUTION_QUERY = "select SUM(dni.quantity*dni.price) as total FROM delivery_note dn INNER JOIN delivery_note_item dni ON dni.delivery_note_id = dn.id WHERE dn.issued_timestamp >= %d AND dn.issued_timestamp <= %d";
	private static final String MONTHLY_EVOLUTION_FILTERED_QUERY = "select SUM(dni.quantity*dni.price) as total FROM delivery_note dn INNER JOIN delivery_note_item dni ON dni.delivery_note_id = dn.id INNER JOIN product pr ON dni.product_id = pr.id WHERE dn.issued_timestamp >= %d AND dn.issued_timestamp <= %d AND pr.code IN (%s)";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
			.withLocale(new Locale("es", "ES"));

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<StatisticsDto> getStatistics() {

		List<StatisticsDto> statistics = new ArrayList<>();

		statistics.add(
				new StatisticsDto(TOTAL_CUSTOMERS, jdbcTemplate.queryForObject(TOTAL_CUSTOMERS_QUERY, String.class)));
		statistics.add(
				new StatisticsDto(TOTAL_INVOICES, jdbcTemplate.queryForObject(TOTAL_INVOICES_QUERY, String.class)));
		statistics.add(new StatisticsDto(TOTAL_DELIVERY_NOTES,
				jdbcTemplate.queryForObject(TOTAL_DELIVERY_NOTES_QUERY, String.class)));

		return statistics;
	}

	public List<RankingDto> getRanking() {
		List<Map<String, Object>> rankingCustomerResult = jdbcTemplate.queryForList(RANKING_CUSTOMER_QUERY);
		return rankingCustomerResult.stream()
				.map(row -> new RankingDto(row.get("alias").toString(), BigDecimal.valueOf((double) row.get("total"))))
				.collect(Collectors.toList());
	}

	public List<MonthlyEvolutionDto> getMonthlyEvolution(List<Integer> productCodes) {

		List<MonthlyEvolutionDto> result = new ArrayList<MonthlyEvolutionDto>();

		LocalDate currentMonth = LocalDate.now();

		for (int i = 0; i < 12; i++) {
			ZonedDateTime minDateTime = currentMonth.minusMonths(i).withDayOfMonth(1).atStartOfDay(ZoneId.of("UTC"));
			ZonedDateTime maxDateTime = minDateTime.toLocalDate()
					.withDayOfMonth(minDateTime.toLocalDate().lengthOfMonth()).plusDays(1)
					.atStartOfDay(ZoneId.of("UTC")).minusNanos(1);

			Long minTimestamp = minDateTime.toInstant().toEpochMilli();
			Long maxTimestamp = maxDateTime.toInstant().toEpochMilli();

			Optional<Double> totalDouble;
			if (productCodes == null || productCodes.isEmpty()) {
				totalDouble = Optional.ofNullable(jdbcTemplate.queryForObject(
						String.format(MONTHLY_EVOLUTION_QUERY, minTimestamp, maxTimestamp), Double.class));
			} else {
				String inSql = String.join(",", Collections.nCopies(productCodes.size(), "?"));
				totalDouble = Optional.ofNullable(jdbcTemplate.queryForObject(
						String.format(MONTHLY_EVOLUTION_FILTERED_QUERY, minTimestamp, maxTimestamp, inSql),
						productCodes.toArray(), Double.class));
			}

			result.add(new MonthlyEvolutionDto(minDateTime.format(formatter),
					BigDecimal.valueOf(totalDouble.orElse(new Double(0)))));
		}

		Collections.reverse(result);
		return result;
	}
}