package com.pepe.albarapp.service.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingDto {

	private String customerAlias;
	private Double invoiceTotal;

}
