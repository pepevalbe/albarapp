package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_BILLING_USER')")
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

	long count();

	String CUSTOMER_TIMESTAMP_CONDITION = "" +
			"(?1 is null or i.customer.code = ?1) and " +
			"(?2 is null or i.issuedTimestamp >= ?2) and " +
			"(?3 is null or i.issuedTimestamp <= ?3)";

	String CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION = "" +
			"(?1 is null or i.customer.code = ?1) and " +
			"(?2 is null or i.issuedTimestamp >= ?2) and " +
			"(?3 is null or i.issuedTimestamp <= ?3) and " +
			"dni.product.code in ?4";

	@Query(value = "select distinct i from Invoice i " +
			"join fetch i.customer c " +
			"join fetch i.deliveryNotes dn " +
			"join fetch dn.deliveryNoteItems dni " +
			"join fetch dni.product p " +
			"where " + CUSTOMER_TIMESTAMP_CONDITION)
	List<Invoice> findIdByCustomerCodeAndTimestampRange(Integer customerCode, Long timestampFrom, Long timestampTo);

	@Query(value = "select distinct i from Invoice i " +
			"join fetch i.customer c " +
			"join fetch i.deliveryNotes dn " +
			"join fetch dn.deliveryNoteItems dni " +
			"join fetch dni.product p " +
			"where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION)
	List<Invoice> findIdByCustomerCodeAndTimestampRangeAndProducts(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes);

	@Query(value = "select i from Invoice i join fetch i.customer c where " + CUSTOMER_TIMESTAMP_CONDITION,
			countQuery = "select count(i) from Invoice i where " + CUSTOMER_TIMESTAMP_CONDITION)
	Page<Invoice> findIdByCustomerCodeAndTimestampRange(Integer customerCode, Long timestampFrom, Long timestampTo, Pageable pageable);

	@Query(value = "select distinct i from Invoice i join fetch i.customer c join i.deliveryNotes dn join dn.deliveryNoteItems dni where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION,
			countQuery = "select count(distinct i.id) from Invoice i join i.deliveryNotes dn join dn.deliveryNoteItems dni where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION)
	Page<Invoice> findIdByCustomerCodeAndTimestampRangeAndProducts(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes, Pageable pageable);

	@Query(value = "select distinct i from Invoice i " +
			"join fetch i.customer c " +
			"join fetch i.deliveryNotes dn " +
			"join fetch dn.deliveryNoteItems dni " +
			"join fetch dni.product p " +
			"where i.id in ?1")
	List<Invoice> findByIdIn(List<Long> ids, Sort sort);

	Page<Invoice> findByIdBetween(Long idFrom, Long idTo, Pageable pageable);

	List<Invoice> findByIdBetween(Long idFrom, Long idTo);
}
