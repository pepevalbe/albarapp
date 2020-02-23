package com.pepe.albarapp.persistence.domain;

import lombok.Data;

import javax.persistence.Column;
import java.util.Objects;

@Data
public class CustomerAecocInfo {

	@Column
	private final String receiverGln;
	@Column
	private final String buyerGln;
	@Column
	private final String shipGln;
	@Column
	private final String payerGln;
	@Column
	private final String invoiceeGln;

	public CustomerAecocInfo(String receiverGln, String buyerGln, String shipGln, String payerGln, String invoiceeGln) {

		this.receiverGln = Objects.requireNonNull(receiverGln);
		this.buyerGln = Objects.requireNonNull(buyerGln);
		this.shipGln = Objects.requireNonNull(shipGln);
		this.payerGln = Objects.requireNonNull(payerGln);
		this.invoiceeGln = Objects.requireNonNull(invoiceeGln);
	}
}
