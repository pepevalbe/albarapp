package com.pepe.albarapp.service.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HensBatchReportDto {

	private String id;
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
	private double maxTemperature;
	private double minTemperature;
	private Long waterReading;
	private Long poultryMashAdditionQuantity;
	private Long poultryMashAdditionFeedTurn;
	private Long poultryMashMaxFeedTurns;
	private String comments;
}
