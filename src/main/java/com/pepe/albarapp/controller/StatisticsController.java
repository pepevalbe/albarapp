package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.StatisticsService;
import com.pepe.albarapp.service.dto.StatisticsDto;
import com.pepe.albarapp.service.dto.statistics.MonthlyEvolutionDto;
import com.pepe.albarapp.service.dto.statistics.RankingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log
@RestController
public class StatisticsController {

	private static final String STATISTICS_ENDPOINT = "/api/statistics";
	private static final String RANKING_ENDPOINT = "/api/statistics/ranking";
	private static final String MONTHLY_EVOLUTION_ENDPOINT = "/api/statistics/monthlyEvolution";

	@Autowired
	private StatisticsService statisticsService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(STATISTICS_ENDPOINT)
	public ResponseEntity<List<StatisticsDto>> getStatistics(@RequestParam @Nullable List<Integer> productCodes, @RequestParam @Nullable Integer numberOfMonths) {

		return ResponseEntity.ok(statisticsService.getStatistics(productCodes, numberOfMonths));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(RANKING_ENDPOINT)
	public ResponseEntity<Page<RankingDto>> getRanking(@RequestParam @Nullable List<Integer> productCodes, @RequestParam @Nullable Integer numberOfMonths) {
		return ResponseEntity.ok(statisticsService.getRanking(productCodes, numberOfMonths));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(MONTHLY_EVOLUTION_ENDPOINT)
	public ResponseEntity<List<MonthlyEvolutionDto>> getMonthlyEvolution(
			@RequestParam @Nullable List<Integer> productCodes, @RequestParam(defaultValue = "12") Integer numberOfMonths) {
		return ResponseEntity.ok(statisticsService.getMonthlyEvolution(productCodes, numberOfMonths));
	}

}