package com.pepe.albarapp.persistance;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class CustomerProductPrice {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double offeredPrice;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerProductPrice that = (CustomerProductPrice) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
