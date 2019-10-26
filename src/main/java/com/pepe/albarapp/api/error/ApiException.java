package com.pepe.albarapp.api.error;

public class ApiException extends RuntimeException {

	private final ApiError apiError;

	public ApiException(ApiError apiError) {
		this.apiError = apiError;
	}

	public ApiError getApiError() {
		return apiError;
	}
}
