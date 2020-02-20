package com.pepe.albarapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAecocInfoDto {
    
    private String receiverCln;
    private String buyerCln;
    private String shipCln;
    private String payerCln;
    private String invoiceeCln;

}
