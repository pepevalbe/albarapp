package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

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
    private Long birthDateTimestamp;
    
    @Column(nullable = false)
    private String race;
    
	@Column(nullable = false)
	private Long animalQuantity;
	
	@Column
    private Long endDateTimestamp;

    @OneToMany(mappedBy = "hensBatch")
	private Set<HensBatchDailyReport> hensBatchDailyReport;
    
    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HensBatch hensBatch = (HensBatch) o;
		if (this.id == null || hensBatch.id == null) return false;
		return id.equals(hensBatch.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
