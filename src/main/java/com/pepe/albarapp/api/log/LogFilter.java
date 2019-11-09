package com.pepe.albarapp.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("Initializing LogFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		ApiLog.startLoggingContext(httpRequest.getMethod() + " " + httpRequest.getRequestURI());
		logger.info("Incoming request");

		chain.doFilter(request, response);

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ApiLog.updateElapsedTime();
		logger.info("Response sent: " + httpResponse.getStatus());
	}
}