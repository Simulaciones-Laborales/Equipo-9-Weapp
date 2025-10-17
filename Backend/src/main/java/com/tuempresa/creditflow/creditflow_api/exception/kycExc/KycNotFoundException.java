package com.tuempresa.creditflow.creditflow_api.exception.kycExc;

public class KycNotFoundException extends RuntimeException {
    public KycNotFoundException(String message) {
        super(message);
    }
}
