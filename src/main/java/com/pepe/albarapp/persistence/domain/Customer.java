package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Customer {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false)
	private String fiscalId;

	@Column(nullable = false, unique = true)
	private int code;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String alias;

	@Column
	private String phoneNumber;

	@Column
	private String email;

	@Column
	private String address;

	@Column
	private String province;

	@Embedded
	private CustomerAecocInfo customerAecocInfo;        // Optional Customer AECOC additional info

	@OneToMany(mappedBy = "customer")
	@OrderBy("price_index ASC")
	private Set<CustomerProductPrice> customerProductPrices;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Customer customer = (Customer) o;
		if (this.id == null || customer.id == null) return false;
		return id.equals(customer.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public boolean isEdiCustomer() {
		return customerAecocInfo != null && customerAecocInfo.isValid();
	}
}
