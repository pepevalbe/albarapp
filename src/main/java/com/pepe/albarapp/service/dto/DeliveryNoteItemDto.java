package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryNoteItemDto {
	private long quantity;
	private double price;
	private String productId;
	private String productName;
}
