package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Product;
import com.pepe.albarapp.persistence.repository.ProductRepository;
import com.pepe.albarapp.service.dto.CustomerDto;
import com.pepe.albarapp.service.dto.ProductDto;
import com.pepe.albarapp.service.mapping.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {

        return productRepository.findAll().stream().map(productMapper::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(String id) {

        return productMapper.map(productRepository.findById(id).orElseThrow(() -> new ApiException(ApiError.ApiError006)));
    }

    @Transactional
    public ProductDto persistProduct(ProductDto productDto) {

        return productMapper.map(productRepository.save(productMapper.map(productDto)));
    }

}
