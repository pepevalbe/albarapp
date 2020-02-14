package com.pepe.albarapp.service.dto;

import lombok.Data;

@Data
public class DeliveryNoteItemDto {
	private long quantity;
	private double price;
	private String productId;
	private String productName;
}
