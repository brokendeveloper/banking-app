package com.brokendev.backend.common.exceptions;

public class PixTransferNotAllowedException extends RuntimeException {
    public PixTransferNotAllowedException(String message) {
        super(message);
    }
}
