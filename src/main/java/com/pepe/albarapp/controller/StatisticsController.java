package com.pepe.albarapp.controller;

import java.util.List;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.StatisticsService;
import com.pepe.albarapp.service.dto.statistics.MonthlyEvolutionDto;
import com.pepe.albarapp.service.dto.statistics.RankingDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
public class StatisticsController {

    private static final String RANKING_ENDPOINT = "/api/statistics/ranking";
    private static final String MONTHLY_EVOLUTION_ENDPOINT = "/api/statistics/monthlyEvolution";
    
    @Autowired
    private StatisticsService statisticsService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(RANKING_ENDPOINT)
	public ResponseEntity<List<RankingDto>> getRanking() {
		return ResponseEntity.ok(statisticsService.getRanking());
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(MONTHLY_EVOLUTION_ENDPOINT)
	public ResponseEntity<List<MonthlyEvolutionDto>> getMonthlyEvolution() {
		return ResponseEntity.ok(statisticsService.getMonthlyEvolution());
	}
    
}