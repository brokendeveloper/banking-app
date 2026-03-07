package com.brokendev.backend.common.exceptions;

public class InvestmentNotMaturedException extends RuntimeException {
    public InvestmentNotMaturedException(String message) {
        super(message);
    }
}
