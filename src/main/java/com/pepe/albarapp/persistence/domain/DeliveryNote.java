package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
	private List<DeliveryNoteItem> deliveryNoteItems;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DeliveryNote that = (DeliveryNote) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public double getTotal() {
		return getGrossTotal() + getTaxTotal();
	}

	public double getGrossTotal() {
		return deliveryNoteItems.stream().mapToDouble(DeliveryNoteItem::getGrossTotal).sum();
	}

	public double getTaxTotal() {
		return deliveryNoteItems.stream().mapToDouble(DeliveryNoteItem::getTaxTotal).sum();
	}
}
