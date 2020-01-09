package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
public interface InvitationRepository extends CrudRepository<Invitation, String> {

	Optional<Invitation> findByToken(String token);

	void deleteByToken(String token);
}
