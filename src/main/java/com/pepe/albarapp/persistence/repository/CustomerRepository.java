package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface CustomerRepository extends CrudRepository<Customer, String> {

	Optional<Customer> findByCode(int code);

	List<Customer> findByAliasContaining(String alias);

	List<Customer> findAll();
}
