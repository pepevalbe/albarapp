package com.pepe.albarapp.persistance;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class DeliveryNoteItem {

    @Id
    private long id;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "product-id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "delivery-note-id", nullable = false)
    private DeliveryNote deliveryNote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryNoteItem that = (DeliveryNoteItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
