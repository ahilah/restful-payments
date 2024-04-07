package com.payments.restpayments.exception;

public class BlockedAccountException extends Exception {
    public BlockedAccountException(String message) {
        super(message);
    }
}