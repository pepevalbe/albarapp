package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.service.ProductService;
import com.pepe.albarapp.service.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
public class ProductController {

    private static final String PRODUCTS_ENDPOINT = "/api/products";

    @Autowired
    private ProductService productService;

    @GetMapping(PRODUCTS_ENDPOINT + "/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping(PRODUCTS_ENDPOINT)
    public ResponseEntity<List<ProductDto>> getProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping(PRODUCTS_ENDPOINT)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {

        return ResponseEntity.ok(productService.persistProduct(productDto));
    }

    @PutMapping(PRODUCTS_ENDPOINT + "/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id, @RequestBody ProductDto productDto) {

        return ResponseEntity.ok(productService.persistProduct(productDto));
    }
}
