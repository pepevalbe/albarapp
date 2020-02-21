package com.pepe.albarapp.service.document.aecoc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AecocConstants {

	public static String glnOwner;
	public static String partyNameOwner;
	public static Party partyOwner;
	public static CorporateOffice corporateOfficeOwner;

	public static void init(String tokenizedInfo) {

		String[] info = tokenizedInfo.split(";");

		if (info.length != 6) {
			log.error("Can not parse AECOC constants from tokenized info: " + tokenizedInfo);
			throw new RuntimeException("Can not parse AECOC constants");
		}

		glnOwner = info[0];
		partyNameOwner = info[1];
		partyOwner = new Party(info[0]);
		corporateOfficeOwner = new CorporateOffice(info[0]);
	}
}
