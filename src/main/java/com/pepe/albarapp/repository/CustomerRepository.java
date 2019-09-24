package com.pepe.albarapp.repository;

import com.pepe.albarapp.persistance.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@RepositoryRestResource
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
