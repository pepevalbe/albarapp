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
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface DeliveryNoteRepository extends PagingAndSortingRepository<DeliveryNote, Long> {

	String CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION = "" +
			"(?1 is null or dn.customer.code >= ?1) and " +
			"(?2 is null or dn.issuedTimestamp >= ?2) and " +
			"(?3 is null or dn.issuedTimestamp <= ?3) and " +
			"dni.product.code in ?4";

	@Query("select dn from DeliveryNote dn where " +
			"(:customerCode is null or dn.customer.code = :customerCode) and " +
			"(:timestampFrom is null or dn.issuedTimestamp >= :timestampFrom) and " +
			"(:timestampTo is null or dn.issuedTimestamp <= :timestampTo)")
	Page<DeliveryNote> filterByCustomerCodeAndTimestampRange(@Param("customerCode") Integer customerCode, @Param("timestampFrom") Long timestampFrom, @Param("timestampTo") Long timestampTo, Pageable pageable);

	long count();

	Page<DeliveryNote> findByCustomerCodeAndInvoiceIsNull(Integer customerCode, Pageable pageable);

	Set<DeliveryNote> findByCustomerCodeBetweenAndIssuedTimestampBetweenAndInvoiceIsNull(Integer customerCodeFrom, Integer customerCodeTo, Long timestampFrom, Long timestampTo);

	@Query(value = "select distinct dn from DeliveryNote dn join dn.deliveryNoteItems dni where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION,
	countQuery = "select count(distinct dn.id) from DeliveryNote dn join dn.deliveryNoteItems dni where " + CUSTOMER_TIMESTAMP_PRODUCTS_CONDITION)
	Page<DeliveryNote> findByCustomerAndTimestampRangeAndProducts(Integer customerCode, Long timestampFrom, Long timestampTo, List<Integer> productCodes, Pageable pageable);

}
