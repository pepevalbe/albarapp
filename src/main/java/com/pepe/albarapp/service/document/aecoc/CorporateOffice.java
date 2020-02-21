package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class CorporateOffice {

	private String gln;

	public CorporateOffice(String gln) {
		this.gln = gln;
	}
}
