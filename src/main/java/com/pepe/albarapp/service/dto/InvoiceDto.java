package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {

	private Long id;
	private long issuedTimestamp;
	private String customerId;
	private String customerAlias;
	private String customerName;
	private String customerFiscalId;
	private double total;
	private long productQuantity;

}
