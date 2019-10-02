package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
//@RepositoryRestResource(excerptProjection = InvoiceView.class)
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
}
