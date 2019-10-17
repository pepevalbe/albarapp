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

	@OneToMany(mappedBy = "invoice")
	private List<DeliveryNote> deliveryNotes;

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
}
