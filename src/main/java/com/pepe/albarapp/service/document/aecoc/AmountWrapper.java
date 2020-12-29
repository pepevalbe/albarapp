package com.pepe.albarapp.service.document.aecoc;

import lombok.Data;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Data
public class AmountWrapper {

	private Amount amount;
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(new Locale("en", "EN"));

	public AmountWrapper(BigDecimal amount) {
		NUMBER_FORMAT.setMaximumFractionDigits(3);
		NUMBER_FORMAT.setMinimumFractionDigits(0);
		NUMBER_FORMAT.setGroupingUsed(false);
		this.amount = new Amount(NUMBER_FORMAT.format(amount));
	}
}
