package com.pepe.albarapp.persistence;

import com.pepe.albarapp.persistence.domain.*;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

@Component
public class GlobalRepositoryRestConfigurer implements RepositoryRestConfigurer {

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.setBasePath("/hateoas");
		config.exposeIdsFor(Customer.class, CustomerProductPrice.class, DeliveryNote.class, DeliveryNoteItem.class, Invitation.class, Invoice.class, Product.class, User.class);
	}
}
