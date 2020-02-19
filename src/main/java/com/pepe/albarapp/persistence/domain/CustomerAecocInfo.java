package com.pepe.albarapp.persistence.domain;

import lombok.Data;

import javax.persistence.Column;

@Data
public class CustomerAecocInfo {

	@Column
	private String receiverCln;
	@Column
	private String buyerCln;
	@Column
	private String shipCln;
	@Column
	private String payerCln;
	@Column
	private String invoiceeCln;

	public boolean isValid() {
		return receiverCln != null && buyerCln != null && shipCln != null && payerCln != null && invoiceeCln != null;
	}
}
