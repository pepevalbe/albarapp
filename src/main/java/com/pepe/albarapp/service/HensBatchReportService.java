package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.HensBatch;
import com.pepe.albarapp.persistence.domain.HensBatchReport;
import com.pepe.albarapp.persistence.repository.HensBatchReportRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.LocalTime;

@Slf4j
@Service
public class HensBatchReportService {

	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
	public static final long MAX_DAY_LAPS = 5;

	@Autowired
	private HensBatchReportRepository hensBatchReportRepository;

	@Autowired
	private HensBatchReportMapper hensBatchReportMapper;


	@Transactional(readOnly = true)
	public HensBatchReportDto getHensBatchReport(String hensBatchReportId) {

		// Get hens batch report
		HensBatchReport hensBatchReport = hensBatchReportRepository.findById(hensBatchReportId).orElseThrow(() -> new ApiException(ApiError.ApiError006));

		HensBatchReportDto hensBatchReportDto = hensBatchReportMapper.map(hensBatchReport);
		return hensBatchReportDto;
	}

	@Transactional(readOnly = true)
	public Set<HensBatchReportDto> getAllHensBatchReports(String hensBatchId) {

		// Get hens batch reports for the given hens batch id
		Set<HensBatchReport> hensBatchReports = hensBatchReportRepository.findByHensBatchId(hensBatchId);

		if (hensBatchReports.isEmpty()) {
			return Collections.emptySet();
		}

		return hensBatchReports.stream().map(hensBatchReportMapper::map).collect(Collectors.toSet());
	}

	@Transactional
	public HensBatchReportDto createHensBatchReport(HensBatchReportDto hensBatchReportDto) {

		// Avoid two reports in the same day
		ZonedDateTime startOfDay = Instant.ofEpochMilli(hensBatchReportDto.getReportTimestamp()).atZone(ZoneId.of("UTC")).toLocalDate().atStartOfDay(ZoneId.of("UTC"));
		ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
		Set<HensBatchReport> reportsSameDay = hensBatchReportRepository.findByHensBatchIdAndReportTimestampBetween(hensBatchReportDto.getHensBatchId(), startOfDay.toInstant().toEpochMilli(), endOfDay.toInstant().toEpochMilli());
		if (reportsSameDay != null && !reportsSameDay.isEmpty()) throw new ApiException(ApiError.ApiError012);


		// Calculate water consumptions if reading is provided
		if (hensBatchReportDto.getWaterReading() != null) {
			// Search for past report with a valid water reading
			Optional<HensBatchReport> lastReportWithWaterReading = hensBatchReportRepository.findFirstByReportTimestampBeforeAndWaterReadingNotNullOrderByReportTimestampDesc(hensBatchReportDto.getReportTimestamp());
			lastReportWithWaterReading.ifPresent(lastReport -> {
				// If a past report is found calculates water consumption and updates any report between that past report (excluding that one) and current report
				Long waterConsumption = Math.round((hensBatchReportDto.getWaterReading() - lastReport.getWaterReading()) / ((hensBatchReportDto.getReportTimestamp() - lastReport.getReportTimestamp()) / (double) ONE_DAY_MILLIS));
				Set<HensBatchReport> pastReports = hensBatchReportRepository.findByHensBatchIdAndReportTimestampBetween(hensBatchReportDto.getHensBatchId(), lastReport.getReportTimestamp(), hensBatchReportDto.getReportTimestamp());
				pastReports = pastReports.stream().map(pastReport -> {
					pastReport.setWaterConsumption(waterConsumption);
					return pastReport;
				}).collect(Collectors.toSet());
				hensBatchReportRepository.saveAll(pastReports);
			});

			// Search for future report (later to current report) with a valid water reading
			Optional<HensBatchReport> nextReportWithWaterReading = hensBatchReportRepository.findFirstByReportTimestampAfterAndWaterReadingNotNullOrderByReportTimestampAsc(hensBatchReportDto.getReportTimestamp());
			nextReportWithWaterReading.ifPresent(nextReport -> {
				// If a past report is found calculates water consumption and updates any report between that current report and later report
				Long waterConsumption = Math.round((nextReport.getWaterReading() - hensBatchReportDto.getWaterReading()) / ((nextReport.getReportTimestamp() - hensBatchReportDto.getReportTimestamp()) / (double) ONE_DAY_MILLIS));
				Set<HensBatchReport> futureReports = hensBatchReportRepository.findByHensBatchIdAndReportTimestampBetween(hensBatchReportDto.getHensBatchId(), hensBatchReportDto.getReportTimestamp(), nextReport.getReportTimestamp());
				futureReports = futureReports.stream().map(futureReport -> {
					if (futureReport.getId() != nextReport.getId()) {
						futureReport.setWaterConsumption(waterConsumption);
					}
					return futureReport;
				}).collect(Collectors.toSet());
				hensBatchReportRepository.saveAll(futureReports);
				hensBatchReportDto.setWaterConsumption(waterConsumption);
			});
		} else {
			Optional<HensBatchReport> nextReportWithWaterReading = hensBatchReportRepository.findFirstByReportTimestampAfterAndWaterReadingNotNullOrderByReportTimestampAsc(hensBatchReportDto.getReportTimestamp());
			nextReportWithWaterReading.ifPresent(nextReport -> {
				hensBatchReportDto.setWaterConsumption(nextReport.getWaterConsumption());
			});
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
