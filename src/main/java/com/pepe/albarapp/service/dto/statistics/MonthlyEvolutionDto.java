package com.pepe.albarapp.service.dto.statistics;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyEvolutionDto {

	private String monthName;
    private BigDecimal invoiceTotal;
    
}
