package com.pepe.albarapp.api.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiError {
	ApiError001("ApiError001", 500, "Generic API Error", "Ups, algo ha ido mal"),
	ApiError100("ApiError100", 500, "Global API Error", "Ups, algo ha ido mal"),
	ApiError002("ApiError002", 404, "Endpoint not found", "Endpoint no encontrado"),
	ApiError003("ApiError003", 401, "Bad credentials", "Credenciales incorrectos"),
	ApiError004("ApiError004", 401, "Bad or missing token", "Token inválido"),
	ApiError005("ApiError005", 400, "Bad input parameters", "Ups, algo ha ido mal"),
	ApiError006("ApiError006", 500, "Error parsing JSON", "Ups, algo ha ido mal"),
	ApiError007("ApiError007", 500, "Client error in provider", "Ups, algo ha ido mal"),
	ApiError008("ApiError008", 500, "Server error in provider", "Ups, algo ha ido mal"),
	ApiError009("ApiError009", 500, "Unexpected error in provider", "Ups, algo ha ido mal");

	final String errorCode;
	@JsonIgnore
	final int httpCode;
	@JsonIgnore
	final String errorDescription;
	final String errorMessage;

	ApiError(String errorCode, int httpCode, String errorDescription, String errorMessage) {
		this.errorCode = errorCode;
		this.httpCode = httpCode;
		this.errorDescription = errorDescription;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String toString() {
		return "ApiError{" +
				"errorCode='" + errorCode + '\'' +
				", httpCode=" + httpCode +
				", errorDescription='" + errorDescription + '\'' +
				", errorMessage='" + errorMessage + '\'' +
				'}';
	}
}
