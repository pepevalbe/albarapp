package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.HensBatchReport;
import com.pepe.albarapp.persistence.repository.HensBatchReportRepository;
import com.pepe.albarapp.service.dto.report.HensBatchReportDto;
import com.pepe.albarapp.service.mapping.HensBatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HensBatchReportService {

	@Autowired
	private HensBatchReportRepository hensBatchReportRepository;

	@Autowired
	private HensBatchMapper hensBatchMapper;

	@Transactional(readOnly = true)
	public HensBatchReportDto getHensBatchReport(String hensBatchReportId) {

		return hensBatchReportRepository.findById(hensBatchReportId)
				.map(hensBatchReport -> hensBatchMapper.map(hensBatchReport))
				.orElseThrow(() -> new ApiException(ApiError.ApiError006));
	}

	@Transactional(readOnly = true)
	public HensBatchReportDto getLastHensBatchReportWithWaterReading(String hensBatchId, long reportTimestamp) {
		return hensBatchReportRepository.findFirstByHensBatchIdAndReportTimestampBeforeOrderByReportTimestampDesc(hensBatchId, reportTimestamp)
				.map(hensBatchReport -> hensBatchMapper.map(hensBatchReport))
				.orElse(null);
	}

	@Transactional(readOnly = true)
	public Set<HensBatchReportDto> getAllHensBatchReports(String hensBatchId) {

		// Get hens batch reports for the given hens batch id
		Set<HensBatchReport> hensBatchReports = hensBatchReportRepository.findByHensBatchId(hensBatchId);

		if (hensBatchReports.isEmpty()) {
			return Collections.emptySet();
		}

		return hensBatchReports.stream().map(hensBatchMapper::map).collect(Collectors.toSet());
	}

	@Transactional
	public HensBatchReportDto createHensBatchReport(HensBatchReportDto hensBatchReportDto) {

		// Avoid two reports in the same day
		ZonedDateTime startOfDay = Instant.ofEpochMilli(hensBatchReportDto.getReportTimestamp()).atZone(ZoneId.of("UTC")).toLocalDate().atStartOfDay(ZoneId.of("UTC"));
		ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
		Set<HensBatchReport> reportsSameDay = hensBatchReportRepository.findByHensBatchIdAndReportTimestampBetween(hensBatchReportDto.getHensBatchId(), startOfDay.toInstant().toEpochMilli(), endOfDay.toInstant().toEpochMilli());

		if (!reportsSameDay.isEmpty()) {
			throw new ApiException(ApiError.ApiError012);
		}

		// Create hens batch report
		HensBatchReport createdHensBatchReport = hensBatchReportRepository.save(hensBatchMapper.map(hensBatchReportDto));
		return hensBatchMapper.map(createdHensBatchReport);
	}

	@Transactional
	public HensBatchReportDto updateHensBatchReport(HensBatchReportDto hensBatchReportDto) {

		// Get hens batch report
		HensBatchReport hensBatchReport = hensBatchReportRepository.findById(hensBatchReportDto.getId())
				.orElseThrow(() -> new ApiException(ApiError.ApiError006));

		// Ignore received report timestamp and use existing timestamp
		hensBatchReportDto.setReportTimestamp(hensBatchReport.getReportTimestamp());

		// Update hens batch report
		HensBatchReport updatedHensBatchReport = hensBatchReportRepository.save(hensBatchMapper.map(hensBatchReportDto));
		return hensBatchMapper.map(updatedHensBatchReport);
	}


	@Transactional
	public boolean deleteHensBatchReport(String hensBatchReportId) {

		if (!hensBatchReportRepository.findById(hensBatchReportId).isPresent()) {
			return false;
		}

		hensBatchReportRepository.deleteById(hensBatchReportId);
		return true;
	}
}
