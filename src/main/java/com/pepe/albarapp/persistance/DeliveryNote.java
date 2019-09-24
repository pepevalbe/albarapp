package com.pepe.albarapp.persistance;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class DeliveryNote {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long issuedTimestamp;

    @ManyToOne
    @JoinColumn(name = "customer-id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "invoice-id")
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
