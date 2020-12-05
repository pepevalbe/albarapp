package com.pepe.albarapp.persistence.repository;

import java.util.Set;

import com.pepe.albarapp.persistence.domain.HensBatchReport;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface HensBatchReportRepository extends CrudRepository<HensBatchReport, String> {
    
    Set<HensBatchReport> findAll();

    Set<HensBatchReport> findByHensBatchId(String hensBatchId);

}
