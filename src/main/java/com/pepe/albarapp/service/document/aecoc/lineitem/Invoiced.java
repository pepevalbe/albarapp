package com.pepe.albarapp.service.document.aecoc.lineitem;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Invoiced {

	@JacksonXmlProperty(isAttribute = true)
	private final String unitOfMeasure = "PCE";

	private String quantity;

	public Invoiced(long quantity) {
		this.quantity = String.valueOf(quantity);
	}
}
