package com.pepe.albarapp.service.document.aecoc.lineitem;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Text {

	@JacksonXmlProperty(isAttribute = true)
	private final String language = "ES";
	@JacksonXmlProperty(isAttribute = true)
	private String type;

	private String text;

	public Text(String text) {
		this.text = text;
	}

	public Text(String text, String type) {
		this.text = text;
		this.type = type;
	}
}
