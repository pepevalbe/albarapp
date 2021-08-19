package com.pepe.albarapp.persistence.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
public class HensBatchExpense {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private long expenseTimestamp;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private boolean recurrent;

    @Column(nullable = false)
    private boolean distribution;

    @ManyToOne
	@JoinColumn(name = "hens_batch_id")
	private HensBatch hensBatch;

}
