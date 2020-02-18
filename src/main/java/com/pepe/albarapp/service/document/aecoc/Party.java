package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Party {

	@JacksonXmlProperty(isAttribute = true)
	private String identificationType;

	private String gln;
	@JacksonXmlProperty(localName = "PartyName")
	private String partyName;

	public Party(String gln, String partyName) {
		this.gln = gln;
		this.partyName = partyName;
	}

	public Party(String gln, String partyName, String identificationType) {
		this.gln = gln;
		this.partyName = partyName;
		this.identificationType = identificationType;
	}
}
