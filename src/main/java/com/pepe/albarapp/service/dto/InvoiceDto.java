package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {

	private Long id;
	private long issuedTimestamp;
	private String customerId;
	private String customerAlias;
	private double total;
}
