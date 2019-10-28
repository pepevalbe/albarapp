package com.pepe.albarapp.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${albarapp.signing_key}")
	private String signingKey;

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Disable use of cookies
		 * Activate CORS configuration with default values and disable CSRF filter
		 * Add authentication and authorization filter
		 * Authorize admin url to role ADMIN
		 * Authorize any other request to roles ADMIN and USER
		 */

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().cors().and().csrf().disable()
				.addFilter(new JWTAuthenticationFilter(authenticationManager(), signingKey))
				.addFilter(new JWTAuthorizationFilter(authenticationManager(), signingKey));

		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");
		http.authorizeRequests().antMatchers("/hateoas/**", "/api/**").hasAnyRole("ADMIN", "USER");
		http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
		//http.headers().frameOptions().sameOrigin();	// Needed for H2 console
		//http.authorizeRequests().anyRequest().permitAll();	// Permit all requests
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(customAuthenticationProvider);
	}
}