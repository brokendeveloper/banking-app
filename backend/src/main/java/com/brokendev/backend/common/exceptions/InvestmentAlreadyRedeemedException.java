package com.brokendev.backend.common.exceptions;

public class InvestmentAlreadyRedeemedException extends RuntimeException {
    public InvestmentAlreadyRedeemedException(String message) {
        super(message);
    }
}
