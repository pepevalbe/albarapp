package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.HensBatchInfoService;
import com.pepe.albarapp.service.HensBatchReportService;
import com.pepe.albarapp.service.dto.report.HensBatchInfoDto;
import com.pepe.albarapp.service.dto.report.HensBatchReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Log
@RestController
@CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class HensBatchController {

	private static final String HENS_BATCH_REPORT_ENDPOINT = "/api/hens-batch-report";
	private static final String HENS_BATCH_INFO_ENDPOINT = "/api/hens-batch-info";

	@Autowired
	private HensBatchReportService hensBatchReportService;

	@Autowired
	private HensBatchInfoService hensBatchInfoService;

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

	@GetMapping(HENS_BATCH_INFO_ENDPOINT)
	public ResponseEntity<List<HensBatchInfoDto>> getHensBatchInfo(@RequestParam String hensBatchId) {

		return ResponseEntity.ok(hensBatchInfoService.getHensBatchInfo(hensBatchId));
	}
}
