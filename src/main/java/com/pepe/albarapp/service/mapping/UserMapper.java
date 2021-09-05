package com.pepe.albarapp.service.mapping;

import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.service.dto.UserDto;

import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target="roles", expression="java(user.getRole().split(\";\"))")
    UserDto map(User user);

    @Mapping(target="role", expression="java(String.join(\";\", user.getRoles()))")
    User map(UserDto user);

}
