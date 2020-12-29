package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class HensBatch {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private long birthTimestamp;

	@Column(nullable = false)
	private String breed;

	@Column(nullable = false)
	private long animalQuantity;

	@Column
	private Long endTimestamp;

	@OneToMany(mappedBy = "hensBatch")
	private Set<HensBatchReport> hensBatchReports;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HensBatch hensBatch = (HensBatch) o;
		return Objects.equals(id, hensBatch.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
