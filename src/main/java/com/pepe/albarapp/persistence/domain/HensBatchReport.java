package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

@Entity
@Data
public class HensBatchReport {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false)
	private long reportTimestamp;

	@Column(nullable = false)
	private Double maxTemperature;

	@Column(nullable = false)
	private Double minTemperature;

	@Column(nullable = false, name = "num_xl")
	private long numXL;

	@Column(nullable = false, name = "num_l")
	private long numL;

	@Column(nullable = false, name = "num_m")
	private long numM;

	@Column(nullable = false, name = "num_s")
	private long numS;

	@Column(nullable = false, name = "num_xs")
	private long numXS;

	@Column(nullable = false)
	private long dirties;

	@Column(nullable = false)
	private long brokens;

	@Column(nullable = false)
	private long deaths;

	@Column(nullable = false)
	private long departures;

	@Column
	private String comments;

	@Column
	private Long waterReading;

	@Column
	private Long poultryMashAdditionQuantity;

	@Column
	private Long poultryMashAdditionFeedTurn;

	@Column
	private Long poultryMashMaxFeedTurns;

	@ManyToOne
	@JoinColumn(name = "hens_batch_id")
	private HensBatch hensBatch;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HensBatchReport that = (HensBatchReport) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public LocalDate getReportLocalDate() {

		return Instant.ofEpochMilli(reportTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
