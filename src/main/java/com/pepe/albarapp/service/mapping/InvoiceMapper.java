package com.pepe.albarapp.service.mapping;

import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.service.dto.DeliveryNoteDto;
import com.pepe.albarapp.service.dto.DeliveryNoteItemDto;
import com.pepe.albarapp.service.dto.InvoiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

	InvoiceDto map(Invoice invoice);

	@Mapping(source = "customer.alias", target = "customerAlias")
	@Mapping(source = "invoice.id", target = "invoiceId")
	@Mapping(source = "total", target = "total")
	@Mapping(ignore = true, target = "customerId")
	DeliveryNoteDto map(DeliveryNote deliveryNote);

	@Mapping(source = "customerId", target = "customer.id")
	DeliveryNote map(DeliveryNoteDto deliveryNoteDto);

	@Mapping(source = "product.name", target = "productName")
	@Mapping(ignore = true, target = "productId")
	DeliveryNoteItemDto map(DeliveryNoteItem deliveryNoteItem);

	@Mapping(source = "productId", target = "product.id")
	DeliveryNoteItem map(DeliveryNoteItemDto deliveryNoteItemDto);
}
