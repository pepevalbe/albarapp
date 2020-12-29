package com.pepe.albarapp.service.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyEvolutionDto {

	private String monthName;
	private BigDecimal invoiceTotal;

}
