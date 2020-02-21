package com.pepe.albarapp.service.document.aecoc.lineitem;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Quantity {

	@JacksonXmlProperty(isAttribute = true)
	private final String unitOfMeasure = "PCE";
    @JacksonXmlText
	private String value;

	public Quantity(long value) {
		this.value = String.valueOf(value);
	}
}
