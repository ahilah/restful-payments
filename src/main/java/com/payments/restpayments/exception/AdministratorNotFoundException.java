package com.payments.restpayments.exception;

public class AdministratorNotFoundException extends RuntimeException {
    public AdministratorNotFoundException(String message) {
        super(message);
    }
}
