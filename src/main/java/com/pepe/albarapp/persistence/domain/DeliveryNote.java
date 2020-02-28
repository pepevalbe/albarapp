package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class DeliveryNote {

	@Id
	@GeneratedValue(generator = "SequentialByYear")
	@GenericGenerator(name = "SequentialByYear", strategy = "com.pepe.albarapp.persistence.SequentialByYearGenerator")
	private Long id;

	@Column
	private String auxDeliveryNoteNr;

	@Column(nullable = false)
	private long issuedTimestamp;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;

	@OneToMany(mappedBy = "deliveryNote")
	private Set<DeliveryNoteItem> deliveryNoteItems;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DeliveryNote that = (DeliveryNote) o;
		if (this.id == null || that.id == null) return false;
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
		return deliveryNoteItems.stream().map(DeliveryNoteItem::getGrossTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal getTaxTotal() {
		return deliveryNoteItems.stream().map(DeliveryNoteItem::getTaxTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
