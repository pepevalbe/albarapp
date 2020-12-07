package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.HensBatch;
import com.pepe.albarapp.persistence.domain.HensBatchReport;
import com.pepe.albarapp.persistence.domain.WaterReading;
import com.pepe.albarapp.persistence.repository.HensBatchReportRepository;
import com.pepe.albarapp.persistence.repository.WaterReadingRepository;
import com.pepe.albarapp.service.dto.report.HensBatchReportDto;
import com.pepe.albarapp.service.mapping.HensBatchReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HensBatchReportService {

	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 60;
	public static final long MAX_DAY_LAPS = 5;

	@Autowired
	private HensBatchReportRepository hensBatchReportRepository;

	@Autowired
	private WaterReadingRepository waterReadingRepository;

	@Autowired
	private HensBatchReportMapper hensBatchReportMapper;


	@Transactional(readOnly = true)
	public HensBatchReportDto getHensBatchReport(String hensBatchReportId) {

		// Get hens batch report
		HensBatchReport hensBatchReport = hensBatchReportRepository.findById(hensBatchReportId).orElseThrow(() -> new ApiException(ApiError.ApiError006));

		// Give some margin to search for water readings
		long timestampFrom = hensBatchReport.getReportTimestamp() - ONE_DAY_MILLIS * 2;
		long timestampTo = hensBatchReport.getReportTimestamp() + ONE_DAY_MILLIS * 2;

		Map<LocalDate, Double> waterReadingsByDate = waterReadingRepository
				.findByHensBatchIdAndTimestampRange(hensBatchReport.getHensBatch().getId(), timestampFrom, timestampTo).stream()
				.collect(Collectors.toMap(WaterReading::getReportLocalDate, WaterReading::getReadingValue));

		// Map report to dto and set water consumption from water readings
		HensBatchReportDto hensBatchReportDto = hensBatchReportMapper.map(hensBatchReport);
		double consumption = getWaterConsumption(hensBatchReport.getReportLocalDate(), waterReadingsByDate);
		hensBatchReportDto.setWaterConsumption(consumption);

		return hensBatchReportDto;
	}

	@Transactional(readOnly = true)
	public Set<HensBatchReportDto> getAllHensBatchReports(String hensBatchId) {

		// Get hens batch reports for the given hens batch id
		Set<HensBatchReport> hensBatchReports = hensBatchReportRepository.findByHensBatchId(hensBatchId);

		if (hensBatchReports.isEmpty()) {
			return Collections.emptySet();
		}

		// Get first and last report timestamps
		long firstReport = hensBatchReports.stream().map(HensBatchReport::getReportTimestamp).min(Long::compareTo).get();
		long lastReport = hensBatchReports.stream().map(HensBatchReport::getReportTimestamp).max(Long::compareTo).get();

		// Give some margin to search for water readings
		firstReport -= ONE_DAY_MILLIS * 2;
		lastReport += ONE_DAY_MILLIS * 2;

		Map<LocalDate, Double> waterReadingsByDate = waterReadingRepository
				.findByHensBatchIdAndTimestampRange(hensBatchId, firstReport, lastReport).stream()
				.collect(Collectors.toMap(WaterReading::getReportLocalDate, WaterReading::getReadingValue));

		// Map reports to dto and set water consumption from water readings
		return hensBatchReports.stream()
				.map(report -> {
					HensBatchReportDto reportDto = hensBatchReportMapper.map(report);
					double consumption = getWaterConsumption(report.getReportLocalDate(), waterReadingsByDate);
					reportDto.setWaterConsumption(consumption);
					return reportDto;
				})
				.collect(Collectors.toSet());
	}

	// For a given date, calculate water consumptions using  water readings from previous and next dates
	private double getWaterConsumption(LocalDate date, Map<LocalDate, Double> waterReadingsByDate) {

		int dayLaps = 1;
		LocalDate currentDate = date;
		LocalDate previousDate = date.minusDays(1);

		while (!waterReadingsByDate.containsKey(currentDate)) {
			currentDate = currentDate.plusDays(1);
			dayLaps++;
			if (dayLaps > MAX_DAY_LAPS) {
				return 0;
			}
		}

		while (!waterReadingsByDate.containsKey(previousDate)) {
			previousDate = previousDate.minusDays(1);
			dayLaps++;
			if (dayLaps > MAX_DAY_LAPS) {
				return 0;
			}
		}

		return (waterReadingsByDate.get(currentDate) - waterReadingsByDate.get(previousDate)) / dayLaps;
	}

	@Transactional
	public HensBatchReportDto createHensBatchReport(HensBatchReportDto hensBatchReportDto) {

		// Check if report already exists for the received same day
		LocalDate receivedDate = Instant.ofEpochMilli(hensBatchReportDto.getReportTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate();
		hensBatchReportRepository.findPreviousReport(hensBatchReportDto.getHensBatchId(), hensBatchReportDto.getReportTimestamp())
				.ifPresent(reportTimestamp -> {
					if (Instant.ofEpochMilli(reportTimestamp).atZone(ZoneId.systemDefault()).toLocalDate().equals(receivedDate)) {
						throw new ApiException(ApiError.ApiError006);
					}
				});
		hensBatchReportRepository.findNextReport(hensBatchReportDto.getHensBatchId(), hensBatchReportDto.getReportTimestamp())
				.ifPresent(reportTimestamp -> {
					if (Instant.ofEpochMilli(reportTimestamp).atZone(ZoneId.systemDefault()).toLocalDate().equals(receivedDate)) {
						throw new ApiException(ApiError.ApiError006);
					}
				});

		// Create new water reading record if received in report
		if (hensBatchReportDto.getWaterConsumption() != null) {
			// Check received water reading integrity
			waterReadingRepository.findPreviousWaterReading(hensBatchReportDto.getHensBatchId(), hensBatchReportDto.getReportTimestamp())
					.ifPresent(previousReading -> {
						if (previousReading > hensBatchReportDto.getWaterConsumption()) {
							throw new ApiException(ApiError.ApiError006);
						}
					});

			waterReadingRepository.findNextWaterReading(hensBatchReportDto.getHensBatchId(), hensBatchReportDto.getReportTimestamp())
					.ifPresent(nextReading -> {
						if (nextReading < hensBatchReportDto.getWaterConsumption()) {
							throw new ApiException(ApiError.ApiError006);
						}
					});

			// Create water reading
			WaterReading waterReading = new WaterReading();
			waterReading.setReadingTimestamp(hensBatchReportDto.getReportTimestamp());
			waterReading.setReadingValue(hensBatchReportDto.getWaterConsumption());
			HensBatch hensBatch = new HensBatch();
			hensBatch.setId(hensBatchReportDto.getHensBatchId());
			waterReading.setHensBatch(hensBatch);
			waterReadingRepository.save(waterReading);
		}

		// Create hens batch report
		HensBatchReport createdHensBatchReport = hensBatchReportRepository.save(hensBatchReportMapper.map(hensBatchReportDto));

		return hensBatchReportMapper.map(createdHensBatchReport);
	}

	public HensBatchReportDto updateHensBatchReport(HensBatchReportDto hensBatchReportDto) {

		// Get hens batch report
		HensBatchReport hensBatchReport = hensBatchReportRepository.findById(hensBatchReportDto.getId()).orElseThrow(() -> new ApiException(ApiError.ApiError006));

		// Update water reading record if received in report
		if (hensBatchReportDto.getWaterConsumption() != null) {
			// TODO
		}

		// Update hens batch report
		HensBatchReport createdHensBatchReport = hensBatchReportRepository.save(hensBatchReportMapper.map(hensBatchReportDto));

		return hensBatchReportMapper.map(createdHensBatchReport);
	}
}
