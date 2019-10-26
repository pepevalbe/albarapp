package com.pepe.albarapp.api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/*
	This class replaces default spring boot servlet error
	It is used to generate specific Api error responses for errors that are not handled in ApiExceptionHandler
 */
@Controller
public class GlobalErrorController implements ErrorController {

	private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class);

	@GetMapping("/error")
	public ResponseEntity<ApiError> handleError(HttpServletRequest request) {

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			int statusCode = Integer.parseInt(status.toString());

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				log.info(ApiError.ApiError002.toString());
				return ResponseEntity.status(ApiError.ApiError002.httpCode).body(ApiError.ApiError002);
			}

			if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
				log.warn(ApiError.ApiError003.toString());
				return ResponseEntity.status(ApiError.ApiError003.httpCode).body(ApiError.ApiError003);
			}

			if (statusCode == HttpStatus.FORBIDDEN.value()) {
				log.warn(ApiError.ApiError004.toString());
				return ResponseEntity.status(ApiError.ApiError004.httpCode).body(ApiError.ApiError004);
			}

		}

		log.error(ApiError.ApiError100.toString());
		return ResponseEntity.status(ApiError.ApiError100.httpCode).body(ApiError.ApiError100);
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}