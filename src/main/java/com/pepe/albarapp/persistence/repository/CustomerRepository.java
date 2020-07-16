package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.service.dto.statistics.RankingDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {

	Optional<Customer> findByCode(int code);

	Set<Customer> findByAliasContaining(String alias);

	Set<Customer> findAll();

	long count();

	@Query(value = "select new com.pepe.albarapp.service.dto.statistics.RankingDto(cus.alias, SUM(dni.quantity*dni.price)) from DeliveryNote dn join dn.customer cus join dn.deliveryNoteItems dni group by cus.id order by SUM(dni.quantity*dni.price) desc")
	Page<RankingDto> findTopByDeliveryNoteTotal(Pageable pageable);
}
