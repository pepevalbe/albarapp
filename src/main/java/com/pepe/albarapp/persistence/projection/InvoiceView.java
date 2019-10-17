package com.pepe.albarapp.persistence.projection;

import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "invoiceView", types = {Invoice.class})
public interface InvoiceView {

	@Value("#{'F' + target.id}")
	String getInvoiceNumber();

	long getIssuedTimestamp();

	Set<DeliveryNote> getDeliveryNotes();
}
