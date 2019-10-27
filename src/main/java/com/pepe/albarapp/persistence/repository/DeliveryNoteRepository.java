package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.DeliveryNote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
//@RepositoryRestResource(excerptProjection = DeliveryNoteView.class)
public interface DeliveryNoteRepository extends CrudRepository<DeliveryNote, Long> {

	List<DeliveryNote> findByIssuedTimestampGreaterThan(long issuedTimestamp);
}
