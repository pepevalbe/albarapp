package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
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
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

	String CUSTOMER_TIMESTAMP_CONDITION = " " +
			"(:customerCode is null or i.customer.code = :customerCode) and " +
			"(:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and " +
			"(:timestampTo is null or i.issuedTimestamp <= :timestampTo) ";

	@Query(value = "select i from Invoice i " +
			"join fetch i.customer c " +
			"join fetch i.deliveryNotes dn " +
			"join fetch dn.deliveryNoteItems dni " +
			"join fetch dni.product p " +
			"where " + CUSTOMER_TIMESTAMP_CONDITION,
			countQuery = "select count(i) from Invoice i where" + CUSTOMER_TIMESTAMP_CONDITION)
	Page<Invoice> filterByCustomerCodeAndTimestampRange(
			@Param("customerCode") Integer customerCode,
			@Param("timestampFrom") Long timestampFrom,
			@Param("timestampTo") Long timestampTo,
			Pageable pageable);

	String CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION = " " +
			"(:customerCode is null or i.customer.code = :customerCode) and " +
			"(:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and " +
			"(:timestampTo is null or i.issuedTimestamp <= :timestampTo) and " +
			"(:productCodes is null or dni.product.code in :productCodes) ";

	@Query(value = "select i from Invoice i " +
			"join fetch i.customer c " +
			"join fetch i.deliveryNotes dn " +
			"join fetch dn.deliveryNoteItems dni " +
			"join fetch dni.product p " +
			"where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION,
			countQuery = "select count(i) from Invoice i " +
					"join i.deliveryNotes dn " +
					"join dn.deliveryNoteItems dni " +
					"where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION)
	Page<Invoice> filterByCustomerCodeAndTimestampRangeAndProducts(
			@Param("customerCode") Integer customerCode,
			@Param("timestampFrom") Long timestampFrom,
			@Param("timestampTo") Long timestampTo,
			@Param("productCodes") List<Integer> productCodes,
			Pageable pageable);
}