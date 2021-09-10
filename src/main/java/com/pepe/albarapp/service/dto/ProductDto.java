package com.pepe.albarapp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String id;
    private int code;
    private String name;
    private double factoryPrice;
    private double tax;
    private String aecocGtin;

}
