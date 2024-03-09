package com.payments.restpayments.transaction;

import com.payments.restpayments.exception.InsufficientFundsException;

public class Account {
    private int id;
    private double balance;
    private boolean isBlocked;

    public Account() {
    }

    public Account(Account account) {
        this.id = account.getId();
        this.balance = account.getBalance();
        this.isBlocked = account.isBlocked();
    }

    public Account(int id, double balance, boolean isBlocked) {
        this.id = id;
        this.balance = balance;
        this.isBlocked = isBlocked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void decreaseBalance(double amount) throws InsufficientFundsException {
        if (balance >= amount) {
            balance -= amount;
        } else {
            throw new InsufficientFundsException("Insufficient funds to make payment.");
        }
    }

    public void increaseBalance(double amount) {
        balance += amount;
    }
}