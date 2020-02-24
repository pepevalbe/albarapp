package com.pepe.albarapp.api.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
	This filter is used to initialize logging context
	doFilter is invoked for every http request
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		ApiLog.startLoggingContext(httpRequest.getMethod() + " " + httpRequest.getRequestURI());
		log.debug("Incoming request");

		chain.doFilter(request, response);

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ApiLog.updateElapsedTime();
		log.debug("Response sent: " + httpResponse.getStatus());
	}
}