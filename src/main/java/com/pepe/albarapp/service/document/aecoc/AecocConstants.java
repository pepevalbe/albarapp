package com.pepe.albarapp.service.document.aecoc;

public class AecocConstants {

	public static String glnOwner;
	public static String partyNameOwner;
	public static Party partyOwner;
	public static CorporateOffice corporateOfficeOwner;

	public static void init(String tokenizedInfo) {

		String[] info = tokenizedInfo.split(";");

		if (info.length != 6) {
			throw new RuntimeException("Can not parse AECOC constants");
		}

		glnOwner = info[0];
		partyNameOwner = info[1];
		partyOwner = new Party(glnOwner, partyNameOwner);
		corporateOfficeOwner = new CorporateOffice(glnOwner, partyNameOwner, info[2], info[3], info[4], info[5]);
	}
}
