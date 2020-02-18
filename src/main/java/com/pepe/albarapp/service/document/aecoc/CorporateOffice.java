package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class CorporateOffice {

	private String gln;
	@JacksonXmlProperty(localName = "PartyName")
	private String partyName;
	@JacksonXmlProperty(localName = "VATregistrationNumber")
	private String vatRegistrationNumber;
	@JacksonXmlProperty(localName = "StreetAndNumber")
	private String streetAndNumber;
	@JacksonXmlProperty(localName = "CityName")
	private String cityName;
	@JacksonXmlProperty(localName = "Postcode")
	private String postcode;

	public CorporateOffice(String gln, String partyName, String vatRegistrationNumber, String streetAndNumber, String cityName, String postcode) {
		this.gln = gln;
		this.partyName = partyName;
		this.vatRegistrationNumber = vatRegistrationNumber;
		this.streetAndNumber = streetAndNumber;
		this.cityName = cityName;
		this.postcode = postcode;
	}
}
