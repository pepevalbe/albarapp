package com.pepe.albarapp.controller;

import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.service.UserService;
import com.pepe.albarapp.service.dto.InvitationDto;
import com.pepe.albarapp.service.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Principal;

@RestController
public class UserController {

	private final static String PROFILE = "/api/profile";
	private final static String INVITATION_ENDPOINT = "/api/send-invitation";
	private final static String USER_ENDPOINT = "/public/user-creation";
	private final static String QUESTION = "/api/question";

	@Value("${albarapp.trivia_url}")
	private String triviaUrl;

	@Autowired
	private UserService userService;

	@GetMapping(PROFILE)
	public ResponseEntity<User> getProfile(Principal principal) {

		return ResponseEntity.ok(userService.getProfile(principal.getName()));
	}

	@PostMapping(INVITATION_ENDPOINT)
	public ResponseEntity sendInvitation(@RequestBody InvitationDto invitationDto) {

		userService.sendInvitation(invitationDto.getEmail(), invitationDto.getRole());

		return ResponseEntity.ok().build();
	}

	@PostMapping(USER_ENDPOINT)
	public ResponseEntity<User> createUser(@RequestBody RegistrationDto registrationDto) {

		return ResponseEntity.ok(userService.createUser(registrationDto));
	}

	@GetMapping(QUESTION)
	public ResponseEntity<String> getQuestion() throws IOException, InterruptedException {

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForEntity(triviaUrl, String.class);
	}
}
