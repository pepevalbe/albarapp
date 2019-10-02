package com.pepe.albarapp.repository;

import com.pepe.albarapp.persistance.DeliveryNote;
import com.pepe.albarapp.persistance.Invoice;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

@Component
public class GlobalRepositoryRestConfigurer implements RepositoryRestConfigurer {

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(Invoice.class, DeliveryNote.class);
	}
}
