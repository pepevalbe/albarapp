package com.pepe.albarapp.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepe.albarapp.api.error.ApiError;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
	This filter is responsible for authentication
	attemptAuthentication is invoked when POST /login is received
	successfulAuthentication is invoked when authentication is successful
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

	final static String ROLE_CLAIMS = "roles";
	private final static String LOGIN_ENDPOINT = "/login";
	private final static String TOKEN_ISSUER = "albarapp";
	private final static long TOKEN_EXPIRATION_TIME_MILLIS = 432000000; // 432000000 milliseconds = 5 day

	private final AuthenticationManager authenticationManager;
	private final String signingKey;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String signingKey) {
		super.setFilterProcessesUrl(LOGIN_ENDPOINT);
		this.authenticationManager = authenticationManager;
		this.signingKey = signingKey;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// Set authentication in context for jpa security method validation
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return authenticationManager.authenticate(authentication);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response, FilterChain chain,
											Authentication auth) {

		List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		String token = Jwts.builder()
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setIssuer(TOKEN_ISSUER)
				.setSubject(auth.getName())
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_MILLIS))
				.addClaims(Collections.singletonMap(ROLE_CLAIMS, roles))
				.signWith(SignatureAlgorithm.HS512, signingKey.getBytes()).compact();

		response.addHeader("Authorization", "Bearer " + token);
		response.addHeader("Access-Control-Expose-Headers", "Authorization");        // For CORS requests
		logger.info("Token generated for: " + auth.getName());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
											  HttpServletResponse response,
											  AuthenticationException failed) throws IOException {

		response.setStatus(ApiError.ApiError003.getHttpCode());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(new ObjectMapper().writeValueAsString(ApiError.ApiError003));
		out.flush();
		out.close();
		logger.warn(ApiError.ApiError003.toString());
	}
}
