package com.pepe.albarapp.service.document.aecoc;

import lombok.Data;

@Data
public class EntityIdentification {

	private String uniqueCreatorIdentification;
	private Party contentOwner;

	public EntityIdentification(String uniqueCreatorIdentification, String gln) {
		this.uniqueCreatorIdentification = uniqueCreatorIdentification;
		contentOwner = new Party(gln, null);
	}
}
