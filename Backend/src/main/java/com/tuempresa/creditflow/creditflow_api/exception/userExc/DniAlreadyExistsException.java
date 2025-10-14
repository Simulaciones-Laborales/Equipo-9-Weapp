package com.tuempresa.creditflow.creditflow_api.exception.userExc;

public class DniAlreadyExistsException extends RuntimeException {
    public DniAlreadyExistsException(String message) {
        super(message);
    }
}
