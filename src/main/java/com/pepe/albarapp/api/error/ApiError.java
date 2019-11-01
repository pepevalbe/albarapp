package com.pepe.albarapp.api.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiError {
	ApiError100("100", 500, "Global API Error", "Ups, algo ha ido mal"),
	ApiError001("001", 500, "Generic API Error", "Ups, algo ha ido mal"),
	ApiError002("002", 404, "Endpoint not found", "Endpoint no encontrado"),
	ApiError003("003", 401, "Bad credentials", "Credenciales incorrectos"),
	ApiError004("004", 403, "Bad or missing token", "Token inválido"),
	ApiError005("005", 403, "Not allowed", "No tienes permisos para acceder al recurso");

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
