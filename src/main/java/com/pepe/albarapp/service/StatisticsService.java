package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.repository.CustomerRepository;
import com.pepe.albarapp.persistence.repository.DeliveryNoteItemRepository;
import com.pepe.albarapp.persistence.repository.DeliveryNoteRepository;
import com.pepe.albarapp.persistence.repository.InvoiceRepository;
import com.pepe.albarapp.service.dto.StatisticsDto;
import com.pepe.albarapp.service.dto.statistics.MonthlyEvolutionDto;
import com.pepe.albarapp.service.dto.statistics.RankingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsService {

	private static final String TOTAL_CUSTOMERS = "Total clientes";
	private static final String TOTAL_INVOICES = "Total facturas";
	private static final String TOTAL_DELIVERY_NOTES = "Total albaranes";
	private static final String AVERAGE_PRICE = "Precio medio de venta";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
			.withLocale(new Locale("es", "ES"));

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private DeliveryNoteItemRepository deliveryNoteItemRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private DeliveryNoteRepository deliveryNoteRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	public List<StatisticsDto> getStatistics(List<Integer> productCodes) {

		List<StatisticsDto> statistics = new ArrayList<>();

		statistics.add(new StatisticsDto(TOTAL_CUSTOMERS, new BigDecimal(customerRepository.count())));
		statistics.add(new StatisticsDto(TOTAL_INVOICES, new BigDecimal(invoiceRepository.count())));
		statistics.add(new StatisticsDto(TOTAL_DELIVERY_NOTES, new BigDecimal(deliveryNoteRepository.count())));
		BigDecimal avgPrice;
		if (productCodes == null || productCodes.isEmpty()) {
			avgPrice = new BigDecimal(deliveryNoteItemRepository.calcAveragePrice());
		} else {
			avgPrice = new BigDecimal(deliveryNoteItemRepository.calcAveragePriceByProductCodes(productCodes));
		}
		avgPrice = avgPrice.setScale(3, RoundingMode.HALF_UP);
		statistics.add(new StatisticsDto(AVERAGE_PRICE, avgPrice));

		return statistics;
	}

	public Page<RankingDto> getRanking(List<Integer> productCodes) {
		Pageable pageable = PageRequest.of(0, 10);
		if (productCodes == null || productCodes.isEmpty()) {
			return customerRepository.findTopByDeliveryNoteTotal(pageable);
		} else {
			return customerRepository.findTopByDeliveryNoteTotalFilteredByProducts(productCodes, pageable);
		}
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
				totalDouble = Optional.ofNullable(
						deliveryNoteItemRepository.calcTotalByIssuedTimestampRange(minTimestamp, maxTimestamp));
			} else {
				totalDouble = Optional.ofNullable(deliveryNoteItemRepository
						.calcTotalByIssuedTimestampRangeAndProductCodes(minTimestamp, maxTimestamp, productCodes));
			}
			result.add(new MonthlyEvolutionDto(minDateTime.format(formatter),
					BigDecimal.valueOf(totalDouble.orElse(new Double(0))).setScale(2, RoundingMode.HALF_UP)));
		}

		Collections.reverse(result);
		return result;
	}
}