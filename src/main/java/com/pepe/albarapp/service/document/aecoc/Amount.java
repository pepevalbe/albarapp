package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Amount {

	@JacksonXmlProperty(isAttribute = true)
	private final String currencyISOcode = "EUR";

	private String amount;

	public Amount(double amount) {
		this.amount = String.valueOf(amount);
	}
}
