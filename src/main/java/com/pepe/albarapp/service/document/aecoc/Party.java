package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Party {

	@JacksonXmlProperty(isAttribute = true)
	private String identificationType;

	private String gln;

	public Party(String gln) {
		this.gln = gln;
	}

	public Party(String gln, String identificationType) {
		this.gln = gln;
		this.identificationType = identificationType;
	}
}
