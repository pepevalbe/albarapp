package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryNoteDto {

	private Long id;
	private String auxDeliveryNoteNr;
	private long issuedTimestamp;
	private String customerId;
	private String customerAlias;
	private String invoiceId;
	private double total;
	private List<DeliveryNoteItemDto> deliveryNoteItems;
}
