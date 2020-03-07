package com.pepe.albarapp.service.dto;

import lombok.Data;

@Data
public class CustomerProductPriceDto {

	private String productId;
	private double offeredPrice;
	private Long priceIndex;
}
