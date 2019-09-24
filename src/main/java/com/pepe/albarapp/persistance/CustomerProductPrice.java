package com.pepe.albarapp.persistance;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
public class CustomerProductPrice {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double offeredPrice;

    @ManyToOne
    @JoinColumn(name = "customer-id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product-id")
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
