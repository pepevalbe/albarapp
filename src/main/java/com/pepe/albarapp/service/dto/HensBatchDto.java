package com.pepe.albarapp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HensBatchDto {
    private String id;
    private String name;
    private long birthTimestamp;
    private String breed;
    private long animalQuantity;
    private Long endTimestamp;
}
