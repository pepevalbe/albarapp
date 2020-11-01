package com.pepe.albarapp.service.mapping;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.CustomerProductPrice;
import com.pepe.albarapp.service.dto.CustomerDto;
import com.pepe.albarapp.service.dto.CustomerProductPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

	CustomerDto map(Customer customer);

	@Mapping(source = "product.id", target = "productId")
	CustomerProductPriceDto map(CustomerProductPrice CustomerProductPrice);

	Customer map(CustomerDto customerDto);

	@Mapping(source = "productId", target = "product.id")
	CustomerProductPrice map(CustomerProductPriceDto CustomerProductPriceDto);
}
