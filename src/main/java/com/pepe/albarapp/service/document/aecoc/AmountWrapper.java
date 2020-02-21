package com.pepe.albarapp.service.document.aecoc;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AmountWrapper {

	private Amount amount;

	public AmountWrapper(BigDecimal amount) {
		this.amount = new Amount(amount.toString());
	}
}
