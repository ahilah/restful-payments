package com.payments.restpayments.exception;

public class IncorrectClientParametersException extends RuntimeException {
    public IncorrectClientParametersException(String message) {
        super(message);
    }
}