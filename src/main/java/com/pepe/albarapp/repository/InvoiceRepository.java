package com.pepe.albarapp.repository;

import com.pepe.albarapp.persistance.Invoice;
import com.pepe.albarapp.projection.InvoiceView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
//@RepositoryRestResource(excerptProjection = InvoiceView.class)
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
}
