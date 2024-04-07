package com.payments.restpayments.transaction;

import java.time.LocalDateTime;
import java.util.Random;

public class Payment {
    private int id;
    private double amount;
    private LocalDateTime timestamp;
    @Deprecated
    private int senderAccountID;
    @Deprecated
    private int receiverAccountID;
    /*private String senderCardNumber;
    private String receiverCardNumber;*/

    public Payment() {
    }

    public Payment(Payment payment) {
        this.id = payment.getId();
        this.amount = payment.getAmount();
        this.timestamp = payment.getTimestamp();
        this.senderAccountID = payment.getSenderAccountID();
        this.receiverAccountID = payment.getReceiverAccountID();
    }

    public Payment(int senderAccountID, int receiverAccountID, double amount) {
        Random random = new Random();
        this.id = random.nextInt(10000000);
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.senderAccountID = senderAccountID;
        this.receiverAccountID = receiverAccountID;
    }

    public Payment(int id, double amount, LocalDateTime timestamp, int senderAccountID, int receiverAccountID) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.senderAccountID = senderAccountID;
        this.receiverAccountID = receiverAccountID;
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

    public int getSenderAccountID() {
        return senderAccountID;
    }

    public void setSenderAccountID(int senderAccountID) {
        this.senderAccountID = senderAccountID;
    }

    public int getReceiverAccountID() {
        return receiverAccountID;
    }

    public void setReceiverAccountID(int receiverAccountID) {
        this.receiverAccountID = receiverAccountID;
    }
}