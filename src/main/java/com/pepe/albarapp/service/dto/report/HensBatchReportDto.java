package com.pepe.albarapp.service.dto.report;

import lombok.Data;

@Data
public class HensBatchReportDto {

	private String id;
	private long reportTimestamp;
	private Long poultryMashConsumption;
	private Double maxTemperature;
	private Double minTemperature;
	private long numXL;
	private long numL;
	private long numM;
	private long numS;
	private long numXS;
	private long dirties;
	private long brokens;
	private long deaths;
	private long departures;
	private Long waterConsumption;
	private Long waterReading;
	private String comments;
	private String hensBatchId;
}
