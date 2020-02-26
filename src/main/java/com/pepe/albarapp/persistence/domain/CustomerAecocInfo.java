package com.pepe.albarapp.persistence.domain;

import lombok.Data;

import javax.persistence.Column;

@Data
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

	public boolean isValid() {
		return receiverGln != null && buyerGln != null && shipGln != null && payerGln != null && invoiceeGln != null;
	}
}
