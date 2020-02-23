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

	@Mapping(source = "customer.id", target = "customerId")
	@Mapping(source = "customer.code", target = "customerCode")
	@Mapping(source = "customer.alias", target = "customerAlias")
	@Mapping(source = "customer.name", target = "customerName")
	@Mapping(source = "customer.fiscalId", target = "customerFiscalId")
	InvoiceDto map(Invoice invoice);

	@Mapping(source = "customer.id", target = "customerId")
	@Mapping(source = "customer.alias", target = "customerAlias")
	@Mapping(source = "invoice.id", target = "invoiceId")
	DeliveryNoteDto map(DeliveryNote deliveryNote);

	@Mapping(source = "customerId", target = "customer.id")
	@Mapping(ignore = true, target = "invoice")
	DeliveryNote map(DeliveryNoteDto deliveryNoteDto);

	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	DeliveryNoteItemDto map(DeliveryNoteItem deliveryNoteItem);

	@Mapping(source = "productId", target = "product.id")
	DeliveryNoteItem map(DeliveryNoteItemDto deliveryNoteItemDto);
}
