package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Data
public class WaterReading {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false)
	private long readingTimestamp;

	@Column(nullable = false)
	private double readingValue;

	@ManyToOne
	@JoinColumn(name = "hens_batch_id")
	private HensBatch hensBatch;

	public LocalDate getReportLocalDate() {

		return Instant.ofEpochMilli(readingTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
