package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.api.security.UserRole;
import com.pepe.albarapp.persistence.domain.Invitation;
import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.persistence.repository.InvitationRepository;
import com.pepe.albarapp.persistence.repository.UserRepository;
import com.pepe.albarapp.service.dto.RegistrationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class UserService {

	private static final long EXPIRATION_TIME_MILLIS = 24 * 60 * 60 * 1000;     // 24h expiration time

	@Value("${albarapp.create_user_url}")
	private String createUserUrl;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InvitationRepository invitationRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User getProfile(String email) {

		return userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ApiError.ApiError001));
	}

	@Transactional
	public void sendInvitation(String email, String role) {

		if (!EmailService.isValidEmail(email)) {
			log.warn("Invalid email!");
			throw new ApiException(ApiError.ApiError006);
		}

		if (!UserRole.contains(role)) {
			log.warn("Invalid role!");
			throw new ApiException(ApiError.ApiError006);
		}

		if (userRepository.findByEmail(email).isPresent()) {
			log.warn("User already exists with this email!");
			throw new ApiException(ApiError.ApiError006);
		}

		Invitation invitation = invitationRepository.save(new Invitation(email, role));
		String link = createUserUrl.concat(invitation.getToken());
		emailService.sendInvitation(email, link);
	}

	@Transactional
	public User createUser(RegistrationDto registrationDto) {

		Invitation invitation = invitationRepository.findByToken(registrationDto.getToken())
				.orElseThrow(() -> new ApiException(ApiError.ApiError007));

		invitationRepository.deleteByToken(registrationDto.getToken());

		if (System.currentTimeMillis() > invitation.getIssuedTimestamp() + EXPIRATION_TIME_MILLIS) {
			log.info("Invitation expired!");
			throw new ApiException(ApiError.ApiError008);
		}

		User user = new User(invitation.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()),
				registrationDto.getName(),
				registrationDto.getSurname(),
				invitation.getRole());

		User createdUser = userRepository.save(user);
		emailService.sendWelcomeEmail(createdUser);

		log.info("User created: " + createdUser.getEmail());
		return createdUser;
	}
}