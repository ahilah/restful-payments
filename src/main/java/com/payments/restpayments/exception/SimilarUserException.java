package com.payments.restpayments.exception;

public class SimilarUserException extends RuntimeException{
    public SimilarUserException(String message) {
        super(message);
    }
}
