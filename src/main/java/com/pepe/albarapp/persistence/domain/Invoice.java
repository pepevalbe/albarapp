package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class Invoice {

	@Id
	@GeneratedValue(generator = "SequentialByYear")
	@GenericGenerator(name = "SequentialByYear", strategy = "com.pepe.albarapp.persistence.SequentialByYearGenerator")
	private Long id;

	@Column(nullable = false)
	private long issuedTimestamp;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER)
	private List<DeliveryNote> deliveryNotes;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Invoice invoice = (Invoice) o;
		return id.equals(invoice.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public double getTotal() {
		return getGrossTotal() + getTaxTotal();
	}

	public double getGrossTotal() {
		return deliveryNotes.stream().mapToDouble(DeliveryNote::getGrossTotal).sum();
	}

	public double getTaxTotal() {
		return deliveryNotes.stream().mapToDouble(DeliveryNote::getTaxTotal).sum();
	}
}
