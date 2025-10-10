package com.tuempresa.creditflow.creditflow_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    CANCELLED;

    // Serializa el enum como su nombre (STRING)
    @JsonValue
    public String toValue() {
        return name();
    }

    // Deserializa tolerante desde JSON (case-insensitive, acepta espacios y guiones)
    @JsonCreator
    public static CreditStatus from(String value) {
        if (value == null) return null;
        String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        try {
            return CreditStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid credit status: " + value);
        }
    }
}

