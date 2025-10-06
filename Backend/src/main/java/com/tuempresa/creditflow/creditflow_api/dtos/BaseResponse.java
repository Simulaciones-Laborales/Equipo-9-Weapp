package com.tuempresa.creditflow.creditflow_api.dtos;

import org.springframework.http.HttpStatus;

public record BaseResponse(
        boolean isError,
        HttpStatus status,
        String message
) {

    // Constructor adicional para compatibilidad
    public BaseResponse(boolean isError, int code, String status, String message) {
        this(isError, HttpStatus.valueOf(code), message);
    }

    // Método para respuestas de error genéricas
    public static BaseResponse error(String message) {
        return new BaseResponse(true, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    // Método para respuestas de error con código personalizado
    public static BaseResponse error(HttpStatus status, String message) {
        return new BaseResponse(true, status, message);
    }

    // Método para respuestas de error de tipo "Bad Request"
    public static BaseResponse badRequest(String message) {
        return new BaseResponse(true, HttpStatus.BAD_REQUEST, message);
    }

    // Método para respuestas exitosas
    public static BaseResponse ok(String message) {
        return new BaseResponse(false, HttpStatus.OK, message);
    }

    // Método para respuestas de creación exitosa
    public static BaseResponse created(String message) {
        return new BaseResponse(false, HttpStatus.CREATED, message);
    }

    // Obtiene el código de estado HTTP
    public int getStatusCode() {
        return status.value();
    }

    // Obtiene el nombre del estado HTTP
    public String getStatusName() {
        return status.getReasonPhrase();
    }
}
