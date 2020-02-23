package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAecocInfoDto {

	private String receiverGln;
	private String buyerGln;
	private String shipGln;
	private String payerGln;
	private String invoiceeGln;
}
