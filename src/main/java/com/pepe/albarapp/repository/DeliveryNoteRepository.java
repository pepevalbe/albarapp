package com.pepe.albarapp.repository;

import com.pepe.albarapp.persistance.DeliveryNote;
import com.pepe.albarapp.projection.DeliveryNoteView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
//@RepositoryRestResource(excerptProjection = DeliveryNoteView.class)
public interface DeliveryNoteRepository extends CrudRepository<DeliveryNote, Long> {
}
