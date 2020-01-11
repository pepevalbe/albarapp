package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.DeliveryNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface DeliveryNoteRepository extends PagingAndSortingRepository<DeliveryNote, Long> {

	@Query(value = "select distinct dn from DeliveryNote dn join fetch dn.deliveryNoteItems where (:customerCode is null or dn.customer.code = :customerCode) and (:timestampFrom is null or dn.issuedTimestamp >= :timestampFrom) and (:timestampTo is null or dn.issuedTimestamp <= :timestampTo)",
	countQuery = "select distinct count(dn) from DeliveryNote dn where (:customerCode is null or dn.customer.code = :customerCode) and (:timestampFrom is null or dn.issuedTimestamp >= :timestampFrom) and (:timestampTo is null or dn.issuedTimestamp <= :timestampTo)")
	Page<DeliveryNote> filterByCustomerCodeAndTimestampRange(@Param("customerCode") Integer customerCode, @Param("timestampFrom") Long timestampFrom, @Param("timestampTo") Long timestampTo, Pageable pageable);

	Page<DeliveryNote> findByCustomerCodeAndInvoiceIsNull(Integer customerCode, Pageable pageable);

	List<DeliveryNote> findByCustomerCodeBetweenAndIssuedTimestampBetweenAndInvoiceIsNull(Integer customerCodeFrom, Integer customerCodeTo, Long timestampFrom, Long timestampTo);
}
