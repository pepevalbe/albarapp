package com.pepe.albarapp;

import com.pepe.albarapp.service.HensBatchInfoService;
import com.pepe.albarapp.service.dto.report.HensBatchInfoDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@WithMockUser(roles = "ADMIN")
@ActiveProfiles(profiles = "development")
@RunWith(SpringRunner.class)
@SpringBootTest
public class HensBatchInfoTests {

	private final static String HENS_BATCH_TEST_ID = "a";

	@Autowired
	HensBatchInfoService hensBatchInfoService;

	@Test
	public void remainingHensTest() {

		// Expected remaining hens according to reports in import.sql
		long[] expectedNumHens = {4999, 4997, 4996, 4993, 4992, 4990, 4959, 4957, 4956};

		List<HensBatchInfoDto> hensBatchInfoDtos = hensBatchInfoService.getHensBatchInfo(HENS_BATCH_TEST_ID);
		long[] numHens = hensBatchInfoDtos.stream().mapToLong(HensBatchInfoDto::getNumHens).toArray();

		Assert.assertArrayEquals(expectedNumHens, numHens);
	}

	@Test
	public void waterConsumptionTest() {

		// Expected water consumptions according to reports in import.sql
		long[] expectedWaterConsumptions = {780, 850, 850, 815, 815, 830, 840, 790, 0};

		List<HensBatchInfoDto> hensBatchInfoDtos = hensBatchInfoService.getHensBatchInfo(HENS_BATCH_TEST_ID);
		long[] waterConsumptions = hensBatchInfoDtos.stream().mapToLong(HensBatchInfoDto::getWaterConsumption).toArray();

		Assert.assertArrayEquals(expectedWaterConsumptions, waterConsumptions);
	}

	@Test
	public void poultryMashConsumptionTest() {

		// Expected poultry mash consumptions according to reports in import.sql
		long[] expectedPoultryMashConsumptions = {0, 524, 524, 524, 541, 553, 553, 553, 0};

		List<HensBatchInfoDto> hensBatchInfoDtos = hensBatchInfoService.getHensBatchInfo(HENS_BATCH_TEST_ID);
		long[] poultryMashConsumptions = hensBatchInfoDtos.stream().mapToLong(HensBatchInfoDto::getPoultryMashConsumption).toArray();

		Assert.assertArrayEquals(expectedPoultryMashConsumptions, poultryMashConsumptions);
	}
}
