package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {
	private String id;
	private String fiscalId;
	private int code;
	private String name;
	private String alias;
	private String phoneNumber;
	private String email;
	private String address;
	private String province;
	private CustomerAecocInfoDto customerAecocInfo;
	private List<CustomerProductPriceDto> customerProductPrices;
}
