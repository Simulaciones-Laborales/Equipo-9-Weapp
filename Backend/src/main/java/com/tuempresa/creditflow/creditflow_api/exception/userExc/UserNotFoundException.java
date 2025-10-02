package com.tuempresa.creditflow.creditflow_api.exception.userExc;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
