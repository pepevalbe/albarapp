package com.pepe.albarapp.api.security;

import com.pepe.albarapp.api.log.ApiLog;
import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Authentication authenticate(Authentication authentication) {
		String email = authentication.getName();
		String password = authentication.getCredentials() != null ? authentication.getCredentials().toString() : null;

		if (email == null || password == null) {
			return null;
		}

		Optional<User> user = userRepository.findByEmailAndPassword(email, password);

		if (user.isPresent()) {
			ApiLog.log(this.getClass(), LogLevel.INFO, "Successful authentication: " + email);
			return new UsernamePasswordAuthenticationToken(email, password, new ArrayList<>());
		}

		ApiLog.log(this.getClass(), LogLevel.WARN, "Failed authentication: " + email);
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
