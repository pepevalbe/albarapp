package com.pepe.albarapp.persistance;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Invoice {

    @Id
    private long id;

    @Column(nullable = false)
    private long invoiceNumber;

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
