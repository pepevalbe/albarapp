package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {
}
