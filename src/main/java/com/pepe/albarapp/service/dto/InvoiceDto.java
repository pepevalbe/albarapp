package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {

	private Long id;
	private long issuedTimestamp;
	private String customerId;
	private int customerCode;
	private String customerAlias;
	private String customerName;
	private String customerFiscalId;
	private double total;
	private long productQuantity;
}
