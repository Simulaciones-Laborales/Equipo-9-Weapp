package com.tuempresa.creditflow.creditflow_api.exception.kycExc;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
