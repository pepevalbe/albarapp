package com.pepe.albarapp.api.error;

import com.pepe.albarapp.api.log.ApiLog;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*
	This class handle exceptions triggered in any controller
	It is used to generate specific Api error responses
 */
@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApiException.class)
	ResponseEntity<Object> handleApiException(ApiException ex) {
		ApiLog.log(this.getClass(), LogLevel.ERROR, ex.getApiError().toString());
		return ResponseEntity.status(ex.getApiError().httpCode).body(ex.getApiError());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Object> handleException(Exception ex) {
		ApiLog.log(this.getClass(), LogLevel.ERROR, ex);
		return ResponseEntity.status(ApiError.ApiError001.httpCode).body(ApiError.ApiError001);
	}
}