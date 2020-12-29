package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.HensBatch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface HensBatchRepository extends CrudRepository<HensBatch, String> {

	Set<HensBatch> findAll();

	Set<HensBatch> findByEndTimestampIsNull();
}
