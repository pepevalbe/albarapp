package com.pepe.albarapp.persistance;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Customer {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String fiscalId;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String alias;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @Column
    private String address;

    @Column
    private String province;

    @OneToMany(mappedBy = "customer")
    private Set<CustomerProductPrice> customerProductPrices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
