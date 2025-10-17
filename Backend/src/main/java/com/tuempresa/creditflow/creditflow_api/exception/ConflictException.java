package com.tuempresa.creditflow.creditflow_api.exception;

/**
 * Se lanza cuando no se puede completar una operación debido a un conflicto 
 * con el estado actual del recurso (HTTP 409).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
    public ConflictException(String message, Throwable cause) { super(message, cause); }
}

