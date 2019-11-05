package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.DeliveryNote;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface DeliveryNoteRepository extends CrudRepository<DeliveryNote, Long> {

	@Query("select dn from DeliveryNote dn where dn.customer.code = ?1 and dn.issuedTimestamp >= ?2 and dn.issuedTimestamp <= ?3 and dn.invoice is null")
	List<DeliveryNote> findDeliveryNotesToBill(int customerCode, long timestampFrom, long timestampTo);
}
