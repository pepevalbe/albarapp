package com.pepe.albarapp.api.security;

import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public Authentication authenticate(Authentication authentication) {
		String email = authentication.getName();
		String password = authentication.getCredentials() != null ? authentication.getCredentials().toString() : null;
		if (email == null || password == null) {
			return null;
		}

		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null || !passwordEncoder().matches(password, user.getPassword())) {
			logger.warn("Failed authentication: " + email);
			return null;
		}

		logger.info("Successful authentication: " + email);
		List<String> stringRoles =  Arrays.asList(user.getRole().split(";"));
		List<SimpleGrantedAuthority> authRoles = stringRoles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(email, password, authRoles);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
