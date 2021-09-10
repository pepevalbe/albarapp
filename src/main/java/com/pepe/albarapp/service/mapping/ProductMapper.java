package com.pepe.albarapp.service.mapping;


import com.pepe.albarapp.persistence.domain.Product;
import com.pepe.albarapp.service.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product map(ProductDto productDto);
    ProductDto map(Product product);

}
