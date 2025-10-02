package com.tuempresa.creditflow.creditflow_api.exception.userExc;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
