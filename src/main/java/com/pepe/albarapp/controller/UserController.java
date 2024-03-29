package com.pepe.albarapp.controller;

import com.pepe.albarapp.api.log.Log;
import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.service.UserService;
import com.pepe.albarapp.service.dto.InvitationDto;
import com.pepe.albarapp.service.dto.RegistrationDto;
import com.pepe.albarapp.service.dto.UserDto;
import com.pepe.albarapp.service.mapping.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Log
@RestController
public class UserController {

	private static final String USER_CREATION_ENDPOINT = "/user-creation";
	private static final String USER_DELETE_ENDPOINT = "/api/user-delete/";
	private static final String USER_ENDPOINT = "/api/users/";
	private static final String PROFILE_ENDPOINT = "/api/profile";
	private static final String INVITATION_ENDPOINT = "/api/send-invitation";
	private static final String TRIVIA_ENDPOINT = "/api/trivia";

	@Autowired
	private UserService userService;

	@PostMapping(USER_CREATION_ENDPOINT)
	public ResponseEntity<User> createUser(@RequestBody RegistrationDto registrationDto) {

		// Add authentication with ROLE_REGISTRY authority
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_REGISTRY");
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("registry", "anonymous", Collections.singleton(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return ResponseEntity.ok(userService.createUser(registrationDto));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(USER_DELETE_ENDPOINT + "{id}")
	public ResponseEntity<Void> deleteUser(Principal principal, @PathVariable String id) {

		userService.deleteUser(principal.getName(), id);

		return ResponseEntity.ok().build();

	}

	@GetMapping(PROFILE_ENDPOINT)
	public ResponseEntity<UserDto> getProfile(Principal principal) {

		return ResponseEntity.ok((userService.getProfile(principal.getName())));
	}

	@GetMapping(USER_ENDPOINT)
	public ResponseEntity<List<UserDto>> getUsers() {

		return ResponseEntity.ok(userService.getUsers());
	}

	@GetMapping(USER_ENDPOINT + "{id}")
	public ResponseEntity<UserDto> getUser(@PathVariable String id) {

		return ResponseEntity.ok(userService.getUser(id));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(USER_ENDPOINT)
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {

		return ResponseEntity.ok(userService.updateUser(userDto));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(INVITATION_ENDPOINT)
	public ResponseEntity<Void> sendInvitation(@RequestBody InvitationDto invitationDto) {

		userService.sendInvitation(invitationDto.getEmail(), invitationDto.getRole());

		return ResponseEntity.ok().build();
	}

	@GetMapping(TRIVIA_ENDPOINT)
	public ResponseEntity<String> getQuestion(@Value("${albarapp.trivia_url}") String triviaUrl) {

		RestTemplate restTemplate = new RestTemplate();
		return ResponseEntity.ok(restTemplate.getForEntity(triviaUrl, String.class).getBody());
	}
}
