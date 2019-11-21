package com.pepe.albarapp.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Product {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false, unique = true)
	private int code;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private double factoryPrice;

	@Column(nullable = false)
	private double tax;

	@JsonIgnore
	@OneToMany(mappedBy = "product")
	private Set<CustomerProductPrice> customerProductPrices;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Product product = (Product) o;
		return id.equals(product.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
