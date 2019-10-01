package com.pepe.albarapp.persistance;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(generator = "SequentialByYear")
    @GenericGenerator(name = "SequentialByYear", strategy = "com.pepe.albarapp.persistance.SequentialByYearIdGenerator")
    private Long id;

    @Column(nullable = false)
    private long issuedTimestamp;

    @OneToMany(mappedBy = "invoice")
    private Set<DeliveryNote> deliveryNotes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return id == invoice.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
