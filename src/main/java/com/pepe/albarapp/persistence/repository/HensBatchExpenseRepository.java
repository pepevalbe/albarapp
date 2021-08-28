package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.HensBatchExpense;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HENS_BATCH_USER')")
public interface HensBatchExpenseRepository extends CrudRepository<HensBatchExpense, String> {

    Set<HensBatchExpense> findByHensBatchId(String hensBatchId);
    
}
