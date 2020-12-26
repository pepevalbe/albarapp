package com.pepe.albarapp.service.dto.report;

import lombok.Data;

@Data
public class HensBatchInfoDto {

	// Direct mapping from hens batch report
	private String hensBatchReportId;
	private String hensBatchId;
	private long reportTimestamp;
	private long numXL;
	private long numL;
	private long numM;
	private long numS;
	private long numXS;
	private long dirties;
	private long brokens;
	private long deaths;
	private long departures;
	private Double maxTemperature;
	private Double minTemperature;
	private Long waterReading;
	private Long poultryMashConsumption;
	private String comments;

	// Calculated attributes
	private Long waterConsumption;
	private long numHens;
	private Long hensWaterConsumption;
}
