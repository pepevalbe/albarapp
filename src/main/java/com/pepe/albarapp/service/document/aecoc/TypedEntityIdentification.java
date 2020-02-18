package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class TypedEntityIdentification {

	@JacksonXmlProperty(isAttribute = true)
	private final String entityType = "INVOICE";

	@JacksonXmlProperty(isAttribute = true)
	private final String type = "COMMERCIAL";

	private EntityIdentification entityIdentification;

	public TypedEntityIdentification(Long uniqueCreatorIdentification, String gln, String partyName) {
		entityIdentification = new EntityIdentification(String.valueOf(uniqueCreatorIdentification), gln, partyName);
	}
}
