package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Objects;

@Data
@NoArgsConstructor
public class CustomerAecocInfo {

	@Column
	private String receiverGln;
	@Column
	private String buyerGln;
	@Column
	private String shipGln;
	@Column
	private String payerGln;
	@Column
	private String invoiceeGln;

	public CustomerAecocInfo(String receiverGln, String buyerGln, String shipGln, String payerGln, String invoiceeGln) {

		this.receiverGln = Objects.requireNonNull(receiverGln);
		this.buyerGln = Objects.requireNonNull(buyerGln);
		this.shipGln = Objects.requireNonNull(shipGln);
		this.payerGln = Objects.requireNonNull(payerGln);
		this.invoiceeGln = Objects.requireNonNull(invoiceeGln);
	}
}
