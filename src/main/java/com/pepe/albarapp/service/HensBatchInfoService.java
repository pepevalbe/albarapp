package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.domain.HensBatchReport;
import com.pepe.albarapp.persistence.repository.HensBatchReportRepository;
import com.pepe.albarapp.service.dto.report.HensBatchInfoDto;
import com.pepe.albarapp.service.mapping.HensBatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HensBatchInfoService {

	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

	@Autowired
	private HensBatchReportRepository hensBatchReportRepository;

	@Autowired
	private HensBatchMapper hensBatchMapper;

	@Transactional(readOnly = true)
	public List<HensBatchInfoDto> getHensBatchInfo(String hensBatchId) {

		// Get hens batch reports order in time
		List<HensBatchReport> hensBatchReports = hensBatchReportRepository.findByHensBatchIdOrderByReportTimestampAsc(hensBatchId);
		if (hensBatchReports.isEmpty()) {
			return Collections.emptyList();
		}

		// Initialize hens batch info with direct mapping from hens batch reports
		List<HensBatchInfoDto> hensBatchInfoDtos = hensBatchReports.stream()
				.map(hensBatchReport -> hensBatchMapper.mapToHensBatchInfo(hensBatchReport))
				.collect(Collectors.toList());

		// Calculate remaining number of hens in batch
		long remainingNumHens = hensBatchReports.get(0).getHensBatch().getAnimalQuantity();
		for (HensBatchInfoDto hensBatchInfoDto : hensBatchInfoDtos) {
			remainingNumHens = remainingNumHens - hensBatchInfoDto.getDepartures() - hensBatchInfoDto.getDeaths();
			hensBatchInfoDto.setNumHens(remainingNumHens);
		}

		// Calculate water consumptions
		hensBatchInfoDtos.forEach(hensBatchInfoDto -> {
			hensBatchInfoDto.setWaterConsumption(calculateWaterConsumption(hensBatchInfoDto.getReportTimestamp(), hensBatchReports));
			hensBatchInfoDto.setHensWaterConsumption(hensBatchInfoDto.getWaterConsumption() / hensBatchInfoDto.getNumHens());
		});

		return hensBatchInfoDtos;
	}

	private long calculateWaterConsumption(long timestamp, List<HensBatchReport> hensBatchReports) {

		// Find last report with water reading
		HensBatchReport lastReport = hensBatchReports.stream()
				.filter(hensBatchReport -> hensBatchReport.getReportTimestamp() < timestamp && hensBatchReport.getWaterReading() != null)
				.max(Comparator.comparing(HensBatchReport::getReportTimestamp))
				.orElse(null);

		// Find next or current report with water reading
		HensBatchReport nextReport = hensBatchReports.stream()
				.filter(hensBatchReport -> hensBatchReport.getReportTimestamp() >= timestamp && hensBatchReport.getWaterReading() != null)
				.min(Comparator.comparing(HensBatchReport::getReportTimestamp))
				.orElse(null);

		if (lastReport == null || nextReport == null) {
			return 0;
		}

		return Math.round((nextReport.getWaterReading() - lastReport.getWaterReading()) / ((nextReport.getReportTimestamp() - lastReport.getReportTimestamp()) / (double) ONE_DAY_MILLIS));
	}
}
