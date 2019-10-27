package com.pepe.albarapp.controller;

import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.service.AdminService;
import com.pepe.albarapp.service.dto.InvitationDto;
import com.pepe.albarapp.service.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final static String INVITATION_ENDPOINT = "/api/send-invitation";
    private final static String USER_ENDPOINT = "/public/user-creation";

    @Autowired
    private AdminService adminService;

    @PostMapping(INVITATION_ENDPOINT)
    public ResponseEntity sendInvitation(@RequestBody InvitationDto invitationDto) {

        adminService.sendInvitation(invitationDto.getEmail(), invitationDto.getRole());

        return ResponseEntity.ok().build();
    }

    @PostMapping(USER_ENDPOINT)
    public ResponseEntity<User> createUser(@RequestBody RegistrationDto registrationDto) {

        return ResponseEntity.ok(adminService.createUser(registrationDto));
    }
}
