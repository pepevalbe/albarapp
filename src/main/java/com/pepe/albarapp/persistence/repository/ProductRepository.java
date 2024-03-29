package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_BILLING_USER')")
public interface ProductRepository extends CrudRepository<Product, String> {

	Set<Product> findAll();
}
