package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

	@Query(value = "select distinct i from Invoice i " +
			"join fetch i.customer c " +
			"join fetch i.deliveryNotes dn " +
			"join fetch dn.deliveryNoteItems dni " +
			"join fetch dni.product p " +
			"where i.id in ?1")
	List<Invoice> findByIdIn(List<Long> ids, Sort sort);

	String CUSTOMER_TIMESTAMP_CONDITION = "" +
			"(:customerCode is null or i.customer.code = :customerCode) and " +
			"(:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and " +
			"(:timestampTo is null or i.issuedTimestamp <= :timestampTo)";

	@Query(value = "select i from Invoice i join fetch i.customer c " +
			"where " + CUSTOMER_TIMESTAMP_CONDITION,
			countQuery = "select count(i) from Invoice i " +
					"where " + CUSTOMER_TIMESTAMP_CONDITION)
	Page<Invoice> findIdByCustomerCodeAndTimestampRange(
			@Param("customerCode") Integer customerCode,
			@Param("timestampFrom") Long timestampFrom,
			@Param("timestampTo") Long timestampTo,
			Pageable pageable);

	String CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION = "dni.product.code in :productCodes and" +
			"(:customerCode is null or i.customer.code = :customerCode) and " +
			"(:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and " +
			"(:timestampTo is null or i.issuedTimestamp <= :timestampTo)";

	@Query(value = "select distinct i from Invoice i join fetch i.customer c join i.deliveryNotes dn join dn.deliveryNoteItems dni " +
			"where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION,
			countQuery = "select count(distinct i.id) from Invoice i join i.deliveryNotes dn join dn.deliveryNoteItems dni " +
					"where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION)
	Page<Invoice> findIdByCustomerCodeAndTimestampRangeAndProducts(
			@Param("customerCode") Integer customerCode,
			@Param("timestampFrom") Long timestampFrom,
			@Param("timestampTo") Long timestampTo,
			@Param("productCodes") List<Integer> productCodes,
			Pageable pageable);

	default Page<Invoice> filter(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes, Pageable pageable) {
		Page<Long> ids;

		if (productCodes == null || productCodes.isEmpty()) {
			ids = findIdByCustomerCodeAndTimestampRange(customerCode, timestampFrom, timestampTo, pageable).map(Invoice::getId);
		} else {
			ids = findIdByCustomerCodeAndTimestampRangeAndProducts(customerCode, timestampFrom, timestampTo, productCodes, pageable).map(Invoice::getId);
		}

		List<Invoice> invoices = findByIdIn(ids.getContent(), pageable.getSort());

		return new PageImpl<>(invoices, pageable, ids.getTotalElements());
	}
}