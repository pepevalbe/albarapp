package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class Amount {

	@JacksonXmlProperty(isAttribute = true)
	private final String currencyISOcode = "EUR";
	@JacksonXmlText
	private String value;

	public Amount(String value) {
		this.value = value;
	}
}
