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
	ApiError005("005", 403, "Not allowed", "No tienes permisos para acceder al recurso"),
	ApiError006("006", 400, "Bad input parameters", "Parámetros de entrada incorrectos"),
	ApiError007("007", 400, "Bad invitation", "Invitación no encontrada"),
	ApiError008("008", 400, "Invitation expired", "La invitación ha caducado"),
	ApiError009("009", 400, "Invoice generation error", "Ha ocurrido un error generando la factura"),
	ApiError010("010", 409, "Customer code already exist", "El código de cliente ya existe"),
	ApiError011("011", 409, "Aecoc invoice generation error", "La factura no existe, el cliente no tiene la información de AECOC o algún producto no tiene GTIN");

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
