package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Invoice {

	@Id
	@GeneratedValue(generator = "SequentialByYear")
	@GenericGenerator(name = "SequentialByYear", strategy = "com.pepe.albarapp.persistence.SequentialByYearGenerator")
	private Long id;

	@Column(nullable = false)
	private long issuedTimestamp;

	@OneToMany(mappedBy = "invoice")
	private Set<DeliveryNote> deliveryNotes;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Invoice invoice = (Invoice) o;
		if (this.id == null || invoice.id == null) return false;
		return id.equals(invoice.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public boolean isEdiInvoice() {
		boolean isValidProducts = deliveryNotes.stream()
				.flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream())
				.noneMatch(item -> item.getProduct().getAecocGtin() == null);

		return customer.isEdiCustomer() && isValidProducts;
	}

	public BigDecimal getTotal() {
		return getGrossTotal().add(getTaxTotal());
	}

	public BigDecimal getGrossTotal() {
		return deliveryNotes.stream().map(DeliveryNote::getGrossTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal getTaxTotal() {
		return deliveryNotes.stream().map(DeliveryNote::getTaxTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public long getProductQuantity() {
		return deliveryNotes.stream()
				.flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream())
				.mapToLong(DeliveryNoteItem::getQuantity)
				.sum();
	}
}
