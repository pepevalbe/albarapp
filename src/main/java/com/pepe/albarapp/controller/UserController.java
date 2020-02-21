package com.pepe.albarapp.controller;

import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.service.StatisticsService;
import com.pepe.albarapp.service.UserService;
import com.pepe.albarapp.service.dto.InvitationDto;
import com.pepe.albarapp.service.dto.RegistrationDto;
import com.pepe.albarapp.service.dto.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
public class UserController {

	private static final String USER_ENDPOINT = "/user-creation";
	private static final String PROFILE_ENDPOINT = "/api/profile";
	private static final String INVITATION_ENDPOINT = "/api/send-invitation";
	private static final String STATISTICS_ENDPOINT = "/api/statistics";
	private static final String TRIVIA_ENDPOINT = "/api/trivia";

	@Autowired
	private UserService userService;

	@Autowired
	private StatisticsService statisticsService;

	@PostMapping(USER_ENDPOINT)
	public ResponseEntity<User> createUser(@RequestBody RegistrationDto registrationDto) {

		// Add authentication with ROLE_REGISTRY authority
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_REGISTRY");
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("registry", "anonymous", Collections.singleton(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return ResponseEntity.ok(userService.createUser(registrationDto));
	}

	@GetMapping(PROFILE_ENDPOINT)
	public ResponseEntity<User> getProfile(Principal principal) {

		return ResponseEntity.ok(userService.getProfile(principal.getName()));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(INVITATION_ENDPOINT)
	public ResponseEntity<Void> sendInvitation(@RequestBody InvitationDto invitationDto) {

		userService.sendInvitation(invitationDto.getEmail(), invitationDto.getRole());

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(STATISTICS_ENDPOINT)
	public ResponseEntity<List<StatisticsDto>> getStatistics() {

		return ResponseEntity.ok(statisticsService.getStatistics());
	}

	@GetMapping(TRIVIA_ENDPOINT)
	public ResponseEntity<String> getQuestion(@Value("${albarapp.trivia_url}") String triviaUrl) {

		RestTemplate restTemplate = new RestTemplate();
		return ResponseEntity.ok(restTemplate.getForEntity(triviaUrl, String.class).getBody());
	}
}
