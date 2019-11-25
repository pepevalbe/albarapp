package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class CustomerProductPriceDto {

	private String productId;
	private double offeredPrice;
}
