package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class IgicTax {

	@JacksonXmlProperty(isAttribute = true)
	private String percentage;

	private AmountWrapper baseAmount;
	private AmountWrapper taxAmount;

	public IgicTax(double grossTotal, double taxTotal, double percentage) {
		this.baseAmount = new AmountWrapper(grossTotal);
		this.taxAmount = new AmountWrapper(taxTotal);
		this.percentage = String.valueOf(percentage);
	}
}
