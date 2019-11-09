package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.DeliveryNote;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface DeliveryNoteRepository extends PagingAndSortingRepository<DeliveryNote, Long> {

	@Query("select dn from DeliveryNote dn where dn.customer.code = :customerCode and dn.issuedTimestamp >= :timestampFrom and dn.issuedTimestamp <= :timestampTo and dn.invoice is null")
	Page<DeliveryNote> findDeliveryNotesToBill(@Param("customerCode") int customerCode,
			@Param("timestampFrom") long timestampFrom, @Param("timestampTo") long timestampTo, Pageable pageable);

	@Query("select dn from DeliveryNote dn where (:customerCode is null or dn.customer.code = :customerCode) and (:timestampFrom is null or dn.issuedTimestamp >= :timestampFrom) and (:timestampTo is null or dn.issuedTimestamp <= :timestampTo)")
	Page<DeliveryNote> findDeliveryNotes(@Param("customerCode") Integer customerCode,
			@Param("timestampFrom") Long timestampFrom, @Param("timestampTo") Long timestampTo, Pageable pageable);
}
