package com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, String description) {
        super(message);
    }
}
