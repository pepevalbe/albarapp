package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Data
public class HensBatchDailyReport {
    
    @Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private Long dateTimestamp;

    @Column
    private Long waterConsumption;

    @Column
    private Long poultryMashConsumption;

    @Column(nullable = false)
    private Double maxTemperature;

    @Column(nullable = false)
    private Double minTemperature;

    @Column(nullable = false)
    private Long XL;

    @Column(nullable = false)
    private Long L;

    @Column(nullable = false)
    private Long M;

    @Column(nullable = false)
    private Long S;

    @Column(nullable = false)
    private Long XS;

    @Column(nullable = false)
    private Long dirties;

    @Column(nullable = false)
    private Long brokens;

    @Column(nullable = false)
    private Long deaths;

    @Column(nullable = false)
    private Long departures;

    @Column
    private String comments;

    @ManyToOne
	@JoinColumn(name = "hens_batch_id")
	private HensBatch hensBatch;

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HensBatchDailyReport hensBatchDailyReport = (HensBatchDailyReport) o;
		if (this.id == null || hensBatchDailyReport.id == null) return false;
		return id.equals(hensBatchDailyReport.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
