package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.service.dto.InvoiceDto;

import java.util.List;

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

	@Query("select new com.pepe.albarapp.service.dto.InvoiceDto(i.id, i.issuedTimestamp, i.customer.id, i.customer.code, i.customer.alias, i.customer.name, i.customer.fiscalId, SUM(dni.quantity*dni.price*(1+dni.product.tax/100)), SUM(dni.quantity)) from Invoice i inner join i.deliveryNotes dn inner join dn.deliveryNoteItems dni where (:customerCode is null or i.customer.code = :customerCode) and (:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and (:timestampTo is null or i.issuedTimestamp <= :timestampTo) group by i.id")
	Page<InvoiceDto> filterByCustomerCodeAndTimestampRange(@Param("customerCode") Integer customerCode, @Param("timestampFrom") Long timestampFrom, @Param("timestampTo") Long timestampTo, Pageable pageable);

	@Query("select new com.pepe.albarapp.service.dto.InvoiceDto(i.id, i.issuedTimestamp, i.customer.id, i.customer.code, i.customer.alias, i.customer.name, i.customer.fiscalId, SUM(dni.quantity*dni.price*(1+dni.product.tax/100)), SUM(dni.quantity)) from Invoice i inner join i.deliveryNotes dn inner join dn.deliveryNoteItems dni where (:customerCode is null or i.customer.code = :customerCode) and (:timestampFrom is null or i.issuedTimestamp >= :timestampFrom) and (:timestampTo is null or i.issuedTimestamp <= :timestampTo) and dni.product.code in (:productCodes) group by i.id")
	Page<InvoiceDto> filterByCustomerCodeAndTimestampRangeAndProducts(@Param("customerCode") Integer customerCode, @Param("timestampFrom") Long timestampFrom, @Param("timestampTo") Long timestampTo, @Param("productCodes") List<Integer> productCodes, Pageable pageable);
}