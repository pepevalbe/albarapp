package com.pepe.albarapp.api.security;

import com.pepe.albarapp.api.log.ApiLog;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/*
	This filter is responsible for authorization
	doFilterInternal is invoked for every request to check token
	If token is not valid request is rejected
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private static final Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

	private final String signingKey;

	public JWTAuthorizationFilter(AuthenticationManager authManager, String signingKey) {
		super(authManager);
		this.signingKey = signingKey;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

		if (authentication != null) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer")) {
			return null;
		}

		String token = authHeader.replace("Bearer ", "");

		Jws<Claims> claims;
		try {
			claims = Jwts.parser().setSigningKey(signingKey.getBytes()).parseClaimsJws(token);
		} catch (JwtException e) {
			logger.error(e.getMessage(), e);
			return null;
		}

		String username = claims.getBody().getSubject();
		List<String> roles = (List<String>) claims.getBody().get(JWTAuthenticationFilter.ROLE_CLAIMS);
		if (username == null || roles == null || roles.isEmpty()) {
			return null;
		}


		List<GrantedAuthority> authorities = roles.stream().map(s -> new SimpleGrantedAuthority("ROLE_".concat(s))).collect(Collectors.toList());

		ApiLog.addUserToLoggingContext(username);
		logger.debug("Successfully checked token");

		return new UsernamePasswordAuthenticationToken(username, null, authorities);
	}
}
