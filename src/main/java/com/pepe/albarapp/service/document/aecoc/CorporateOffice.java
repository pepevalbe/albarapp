package com.pepe.albarapp.service.document.aecoc;

import lombok.Data;

@Data
public class CorporateOffice {

	private String gln;

	public CorporateOffice(String gln) {
		this.gln = gln;
	}
}
