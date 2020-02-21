package com.pepe.albarapp.service.document.aecoc.lineitem;

import lombok.Data;

@Data
public class Invoiced {


	private Quantity quantity;

	public Invoiced(long quantity) {
		this.quantity = new Quantity(quantity);
	}
}
