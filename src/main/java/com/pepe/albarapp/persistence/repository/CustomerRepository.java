package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.service.dto.statistics.RankingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_BILLING_USER')")
public interface CustomerRepository extends CrudRepository<Customer, String> {

	Optional<Customer> findByCode(int code);

	Set<Customer> findByAliasContaining(String alias);

	Set<Customer> findAll();

	long count();

	@Query(value = "select new com.pepe.albarapp.service.dto.statistics.RankingDto(cus.alias, SUM(dni.quantity*dni.price*(1+dni.product.tax/100))) from DeliveryNote dn join dn.customer cus join dn.deliveryNoteItems dni where (?1 is null or dni.deliveryNote.issuedTimestamp >= ?1) and (?2 is null or dni.deliveryNote.issuedTimestamp <= ?2) group by cus.id order by SUM(dni.quantity*dni.price) desc")
	Page<RankingDto> findTopByDeliveryNoteTotal(Long timestampFrom, Long timestampTo, Pageable pageable);

	@Query(value = "select new com.pepe.albarapp.service.dto.statistics.RankingDto(cus.alias, SUM(dni.quantity*dni.price*(1+dni.product.tax/100))) from DeliveryNote dn join dn.customer cus join dn.deliveryNoteItems dni where dni.product.code in ?1 and (?2 is null or dni.deliveryNote.issuedTimestamp >= ?2) and (?3 is null or dni.deliveryNote.issuedTimestamp <= ?3) group by cus.id order by SUM(dni.quantity*dni.price) desc")
	Page<RankingDto> findTopByDeliveryNoteTotalFilteredByProducts(List<Integer> productCodes, Long timestampFrom, Long timestampTo, Pageable pageable);
}
