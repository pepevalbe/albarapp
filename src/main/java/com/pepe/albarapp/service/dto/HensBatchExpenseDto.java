package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HensBatchExpenseDto {
    private String id;
    private String hensBatchId;
    private long expenseTimestamp;
    private String description;
    private double value;
    private boolean recurrent;
    private boolean distribution;
}
