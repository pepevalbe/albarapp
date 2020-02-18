package com.pepe.albarapp.service.document.aecoc.lineitem;

import lombok.Data;

@Data
public class Description {

	private Text description;

	public Description(String description) {
		this.description = new Text(description);
	}
}
