package com.pepe.albarapp.api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*
	This class handle exceptions triggered in any controller
	It is used to generate specific Api error responses
 */
@ControllerAdvice
public class ApiExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	ResponseEntity<Object> handleApiException(ApiException ex) {
		logger.error(ex.getApiError().toString());
		return ResponseEntity.status(ex.getApiError().httpCode).body(ex.getApiError());
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
		logger.error(ApiError.ApiError005.toString());
		return ResponseEntity.status(ApiError.ApiError005.httpCode).body(ApiError.ApiError005);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		throw ex;
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Object> handleException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return ResponseEntity.status(ApiError.ApiError001.httpCode).body(ApiError.ApiError001);
	}
}