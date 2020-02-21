package com.pepe.albarapp.service.document.aecoc;

import lombok.Data;

@Data
public class AmountWrapper {

	private Amount amount;

	public AmountWrapper(double amount) {
		this.amount = new Amount(String.valueOf(amount));
	}
}
