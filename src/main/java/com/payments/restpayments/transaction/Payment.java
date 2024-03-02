package com.payments.restpayments.transaction;

import com.payments.restpayments.exception.BlockedAccountException;
import com.payments.restpayments.exception.InsufficientFundsException;
import com.payments.restpayments.transaction.Account;

import java.time.LocalDateTime;
import java.util.Random;

public class Payment {
    private int id;
    private double amount;
    private LocalDateTime timestamp;
    private int senderCreditCardID;
    private int receiverCreditCardID;

    public Payment() {
    }

    public Payment(Payment payment) {
        this.id = payment.getId();
        this.amount = payment.getAmount();
        this.timestamp = payment.getTimestamp();
        this.senderCreditCardID = payment.getSenderCreditCardID();
        this.receiverCreditCardID = payment.getReceiverCreditCardID();
    }

    public Payment(int senderCreditCardID, int receiverCreditCardID, double amount) {
        Random random = new Random();
        this.id = random.nextInt(1000000);;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.senderCreditCardID = senderCreditCardID;
        this.receiverCreditCardID = receiverCreditCardID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getSenderCreditCardID() {
        return senderCreditCardID;
    }

    public void setSenderCreditCardID(int senderCreditCardID) {
        this.senderCreditCardID = senderCreditCardID;
    }

    public int getReceiverCreditCardID() {
        return receiverCreditCardID;
    }

    public void setReceiverCreditCardID(int receiverCreditCardID) {
        this.receiverCreditCardID = receiverCreditCardID;
    }
}