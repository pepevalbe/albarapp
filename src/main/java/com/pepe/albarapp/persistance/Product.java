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
public class Product {

    @Id
    private long id;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double factoryPrice;

    @OneToMany(mappedBy = "product")
    private Set<CustomerProductPrice> customerProductPrices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
