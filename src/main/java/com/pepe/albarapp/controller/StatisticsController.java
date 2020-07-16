package com.pepe.albarapp.controller;

import java.util.List;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.StatisticsService;
import com.pepe.albarapp.service.dto.StatisticsDto;
import com.pepe.albarapp.service.dto.statistics.MonthlyEvolutionDto;
import com.pepe.albarapp.service.dto.statistics.RankingDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<List<StatisticsDto>> getStatistics() {

		return ResponseEntity.ok(statisticsService.getStatistics());
	}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(RANKING_ENDPOINT)
    public ResponseEntity<List<RankingDto>> getRanking() {
        return ResponseEntity.ok(statisticsService.getRanking());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(MONTHLY_EVOLUTION_ENDPOINT)
    public ResponseEntity<List<MonthlyEvolutionDto>> getMonthlyEvolution(
            @RequestParam @Nullable List<Integer> productCodes) {
        return ResponseEntity.ok(statisticsService.getMonthlyEvolution(productCodes));
    }

}