package com.pepe.albarapp.api.log;

import org.springframework.boot.logging.LogLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
	This filter is used to initialize logging context
	doFilter is invoked for every http request
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InitLogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApiLog.log(this.getClass(), LogLevel.DEBUG, "Initializing InitLogFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		ApiLog.startLoggingContext(httpRequest.getMethod() + " " + httpRequest.getRequestURI());
		ApiLog.log(this.getClass(), LogLevel.INFO, "Initialized api logging context");

		chain.doFilter(request, response);
	}
}