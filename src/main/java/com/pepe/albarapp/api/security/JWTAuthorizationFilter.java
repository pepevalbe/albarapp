package com.pepe.albarapp.api.security;

import com.pepe.albarapp.api.log.ApiLog;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.logging.LogLevel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*
	This filter is responsible for authorization
	doFilterInternal is invoked for every request to check token
	If token is not valid request is rejected
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
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
		String token = request.getHeader("Authorization");
		System.out.println(token);
		if (token == null || !token.startsWith("Bearer")) {
			return null;
		}

		String username;
		try {
			username = Jwts.parser()
					.setSigningKey("signingkey")
					.parseClaimsJws(token.replace("Bearer ", ""))
					.getBody()
					.getSubject();
		} catch (Exception e) {
			ApiLog.log(JWTAuthorizationFilter.class, LogLevel.ERROR, e);
			return null;
		}

		if (username == null) {
			return null;
		}

		ApiLog.addUserToLoggingContext(username);
		ApiLog.log(JWTAuthorizationFilter.class, LogLevel.DEBUG, "Successfully checked token");
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
		return new UsernamePasswordAuthenticationToken(username, null, authorities);
	}
}
