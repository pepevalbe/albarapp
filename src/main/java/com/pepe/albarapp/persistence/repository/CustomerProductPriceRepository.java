package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.CustomerProductPrice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
public interface CustomerProductPriceRepository extends CrudRepository<CustomerProductPrice, String> {
}
