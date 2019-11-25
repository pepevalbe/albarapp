package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

	@Query("select i from Invoice i where (:customerCode is null or i.customer.code = :customerCode) and (:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and (:timestampTo is null or i.issuedTimestamp <= :timestampTo)")
	Page<Invoice> filterByCustomerCodeAndTimestampRange(@Param("customerCode") Integer customerCode, @Param("timestampFrom") Long timestampFrom, @Param("timestampTo") Long timestampTo, Pageable pageable);
}
