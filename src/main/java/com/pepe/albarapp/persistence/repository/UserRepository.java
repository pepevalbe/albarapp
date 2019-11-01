package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface UserRepository extends CrudRepository<User, String> {

	@PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.principal.username")
	Optional<User> findByEmail(String email);
}
