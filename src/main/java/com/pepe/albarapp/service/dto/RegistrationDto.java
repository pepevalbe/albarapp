package com.pepe.albarapp.service.dto;

import lombok.Data;

@Data
public class RegistrationDto {

    private String token;
    private String name;
    private String surname;
    private String password;
}
