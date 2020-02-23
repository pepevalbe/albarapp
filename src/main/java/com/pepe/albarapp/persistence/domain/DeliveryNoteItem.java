package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Data
public class DeliveryNoteItem {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false)
	private long quantity;

	@Column(nullable = false)
	private double price;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "delivery_note_id", nullable = false)
	private DeliveryNote deliveryNote;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DeliveryNoteItem that = (DeliveryNoteItem) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public BigDecimal getTotal() {
		return getGrossTotal().add(getTaxTotal());
	}

	public BigDecimal getGrossTotal() {
		return new BigDecimal(quantity).multiply(new BigDecimal(price));
	}

	public BigDecimal getTaxTotal() {
		return getGrossTotal().multiply(new BigDecimal(String.valueOf(product.getTax())).multiply(new BigDecimal("0.01")));
	}
}
