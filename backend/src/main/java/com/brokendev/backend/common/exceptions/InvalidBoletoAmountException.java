package com.brokendev.backend.common.exceptions;

public class InvalidBoletoAmountException extends RuntimeException {
    public InvalidBoletoAmountException(String message) {
        super(message);
    }
}
