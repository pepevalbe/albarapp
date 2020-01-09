package com.pepe.albarapp.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
		 * Authorize /hateoas and /api requests to any role
		 */

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().cors().and().csrf().disable()
				.addFilter(new JWTAuthenticationFilter(authenticationManager(), signingKey))
				.addFilter(new JWTAuthorizationFilter(authenticationManager(), signingKey));

		http.authorizeRequests().antMatchers("/hateoas/**", "/api/**").hasAnyRole(UserRole.roles());
		http.authorizeRequests().antMatchers("/user-creation").permitAll();
		//http.headers().frameOptions().sameOrigin();	      // Needed for H2 console
		//http.authorizeRequests().anyRequest().permitAll();  // Permit all requests
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(customAuthenticationProvider);
	}
}