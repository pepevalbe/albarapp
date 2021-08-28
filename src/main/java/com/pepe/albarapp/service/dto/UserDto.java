package com.pepe.albarapp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String id;
    private String email;
	private String name;
	private String surname;
	private String[] roles;
    
}
