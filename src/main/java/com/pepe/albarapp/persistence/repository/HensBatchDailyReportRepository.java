package com.pepe.albarapp.persistence.repository;

import java.util.Set;

import com.pepe.albarapp.persistence.domain.HensBatchDailyReport;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface HensBatchDailyReportRepository extends CrudRepository<HensBatchDailyReport, String> {
    
    Set<HensBatchDailyReport> findAll();

    Set<HensBatchDailyReport> findByHensBatchId(String hensBatchId);

}
