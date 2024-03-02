package com.payments.restpayments.transaction;

import com.payments.restpayments.exception.BlockedAccountException;
import com.payments.restpayments.exception.InsufficientFundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreditCard {
    private int id;
    private String cardNumber;
    private String cardType;
    private Account account;
    private List<Payment> payments = new ArrayList<>();

    public CreditCard() {
    }

    public CreditCard(CreditCard creditCard) {
        this.id = creditCard.getId();
        this.cardNumber = creditCard.getCardNumber();
        this.cardType = creditCard.getCardType();
        this.account = creditCard.getAccount();
        this.payments = creditCard.getPayments();
    }

    public CreditCard(int id, String cardNumber, String cardType, Account account) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Payment processPayment(CreditCard receiverCreditCard, double amount) {
        Payment payment = null;
        try {
            if (account.isBlocked()) {
                throw new BlockedAccountException("Payment cannot be processed. Sender account is blocked.");
            }

            if (account.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient funds to make payment.");
            }

            account.decreaseBalance(amount);
            receiverCreditCard.getAccount().increaseBalance(amount);
            payment = new Payment(account.getId(), receiverCreditCard.getAccount().getId(), amount);
            payments.add(payment);
            System.out.println("Payment of " + amount + " processed successfully from account " + account.getId() +
                    " to account " + receiverCreditCard.getAccount().getId());
        } catch (BlockedAccountException | InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
        return payment;
    }
}