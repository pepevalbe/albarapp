package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class IgicTax {

	@JacksonXmlProperty(isAttribute = true)
	private String percentage;

	private Amount baseAmount;
	private Amount taxAmount;

	public IgicTax(double grossTotal, double taxTotal, double percentage) {
		baseAmount = new Amount(grossTotal);
		taxAmount = new Amount(taxTotal);
		this.percentage = String.valueOf(percentage);
	}
}
