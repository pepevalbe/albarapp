package com.pepe.albarapp;

import com.pepe.albarapp.persistence.domain.HensBatch;
import com.pepe.albarapp.persistence.repository.HensBatchRepository;
import com.pepe.albarapp.service.HensBatchInfoService;
import com.pepe.albarapp.service.HensBatchReportService;
import com.pepe.albarapp.service.dto.report.HensBatchInfoDto;
import com.pepe.albarapp.service.dto.report.HensBatchReportDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@WithMockUser(roles = "ADMIN")
@ActiveProfiles(profiles = "development")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadTests {

	@Autowired
	HensBatchRepository hensBatchRepository;

	@Autowired
	HensBatchReportService hensBatchReportService;

	@Autowired
	HensBatchInfoService hensBatchInfoService;

	@Test
	public void hensBatchInfoLoadTest() {

		int numRecords = 500;

		System.out.println("Creating data for load test: " + numRecords + " records...");
		String hensBatchId = hensBatchRepository.save(createHensBatch()).getId();
		long initialMillis = System.currentTimeMillis();
		LocalDateTime localDateTime = LocalDateTime.now();
		for (int i = 0; i < numRecords; i++) {
			long timestamp = localDateTime.plusDays(i).toInstant(ZoneOffset.UTC).toEpochMilli();
			hensBatchReportService.createHensBatchReport(createReportDto(timestamp, hensBatchId));
		}
		System.out.println("Data created, elapsed time: " + (System.currentTimeMillis() - initialMillis) / 1000d + " seconds");

		System.out.println("Getting all records info...");
		initialMillis = System.currentTimeMillis();
		List<HensBatchInfoDto> hensBatchInfoDtos = hensBatchInfoService.getHensBatchInfo(hensBatchId);
		System.out.println("All records retrieved, elapsed time: " + (System.currentTimeMillis() - initialMillis) / 1000d + " seconds");

		Assert.assertEquals(numRecords, hensBatchInfoDtos.size());
	}


	private HensBatch createHensBatch() {

		HensBatch hensBatch = new HensBatch();
		hensBatch.setName("lote");
		hensBatch.setBreed("raza");
		hensBatch.setBirthTimestamp(System.currentTimeMillis());
		hensBatch.setAnimalQuantity(10000);
		return hensBatch;
	}

	private HensBatchReportDto createReportDto(long timestamp, String hensBatchId) {

		HensBatchReportDto hensBatchReportDto = new HensBatchReportDto();
		hensBatchReportDto.setHensBatchId(hensBatchId);
		hensBatchReportDto.setReportTimestamp(timestamp);
		hensBatchReportDto.setMaxTemperature(28d);
		hensBatchReportDto.setMinTemperature(26d);
		hensBatchReportDto.setWaterReading((long) (Math.random() * 100) + 500);
		return hensBatchReportDto;
	}
}
