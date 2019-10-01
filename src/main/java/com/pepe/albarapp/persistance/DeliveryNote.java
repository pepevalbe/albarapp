package com.pepe.albarapp.persistance;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class DeliveryNote {

    @Id
    @GeneratedValue(generator = "SequentialByYear")
    @GenericGenerator(name = "SequentialByYear", strategy = "com.pepe.albarapp.persistance.SequentialByYearIdGenerator")
    private Long id;

    @Column
    private String auxDeliveryNoteNumber;

    @Column(nullable = false)
    private long issuedTimestamp;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @OneToMany(mappedBy = "deliveryNote")
    private Set<DeliveryNoteItem> deliveryNoteItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryNote that = (DeliveryNote) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
