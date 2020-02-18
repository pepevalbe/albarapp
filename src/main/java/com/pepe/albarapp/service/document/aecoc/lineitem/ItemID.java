package com.pepe.albarapp.service.document.aecoc.lineitem;

import lombok.Data;

@Data
public class ItemID {

	private String gtin;

	public ItemID(String gtin) {
		this.gtin = gtin;
	}
}
