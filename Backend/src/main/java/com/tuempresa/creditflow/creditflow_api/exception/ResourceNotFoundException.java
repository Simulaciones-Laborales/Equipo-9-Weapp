package com.tuempresa.creditflow.creditflow_api.exception;

/**
 *Se lanza cuando no se encuentra el recurso o la entidad solicitados. 
 *Generalmente, se asigna a HTTP 404 mediante GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String message, Throwable cause) { super(message, cause); }
}

