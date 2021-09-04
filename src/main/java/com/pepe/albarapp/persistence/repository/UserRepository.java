package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface UserRepository extends CrudRepository<User, String> {

	Set<User> findAll();

	@PreAuthorize("hasRole('ROLE_ADMIN') or #email == authentication.name")
	Optional<User> findByEmail(String email);

	@PreAuthorize("hasRole('ROLE_REGISTRY')")
	@Override
	<S extends User> S save(S s);
}
