package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.HensBatchReportService;
import com.pepe.albarapp.service.dto.report.HensBatchReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Log
@RestController
public class HensBatchReportController {

	private static final String HENS_BATCH_REPORT_ENDPOINT = "/api/hens-batch-report";

	@Autowired
	private HensBatchReportService hensBatchReportService;

	@GetMapping(HENS_BATCH_REPORT_ENDPOINT)
	public ResponseEntity<Set<HensBatchReportDto>> getHensBatchReports(@RequestParam String hensBatchId) {

		return ResponseEntity.ok(hensBatchReportService.getAllHensBatchReports(hensBatchId));
	}

	@GetMapping(HENS_BATCH_REPORT_ENDPOINT + "/{hensBatchReportId}")
	public ResponseEntity<HensBatchReportDto> getHensBatchReport(@PathVariable String hensBatchReportId) {

		return ResponseEntity.ok(hensBatchReportService.getHensBatchReport(hensBatchReportId));
	}

	@PostMapping(HENS_BATCH_REPORT_ENDPOINT)
	public ResponseEntity<HensBatchReportDto> postHensBatchReport(@RequestBody HensBatchReportDto hensBatchReportDto) {

		return ResponseEntity.ok(hensBatchReportService.createHensBatchReport(hensBatchReportDto));
	}

	@PutMapping(HENS_BATCH_REPORT_ENDPOINT)
	public ResponseEntity<HensBatchReportDto> putHensBatchReport(@RequestBody HensBatchReportDto hensBatchReportDto) {

		return ResponseEntity.ok(hensBatchReportService.updateHensBatchReport(hensBatchReportDto));
	}

	@DeleteMapping(HENS_BATCH_REPORT_ENDPOINT + "/{hensBatchReportId}")
	public ResponseEntity<Void> deleteHensBatchReport(@PathVariable String hensBatchReportId) {

		return hensBatchReportService.deleteHensBatchReport(hensBatchReportId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();

	}
}
