package com.payments.restpayments.transaction;

import com.payments.restpayments.exception.BlockedAccountException;
import com.payments.restpayments.exception.InsufficientFundsException;
import com.payments.restpayments.exception.SimilarCardSenderReceiverException;
import com.payments.restpayments.role.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;


public class CreditCard {
    private static final Logger logger = LogManager.getLogger(CreditCard.class);
    private int cardID;
    private String clientID;
    private String cardNumber;
    private String cardType;
    private Account account;
    private List<Payment> payments = new ArrayList<>();

    public CreditCard() {
    }

    public CreditCard(@NotNull CreditCard creditCard) {
        this.cardID = creditCard.getCardID();
        this.clientID = creditCard.getClientID();
        this.cardNumber = creditCard.getCardNumber();
        this.cardType = creditCard.getCardType();
        this.account = new Account(creditCard.getAccount());
        this.payments = creditCard.getPayments();
    }

    @Deprecated(forRemoval = true)
    public CreditCard(int cardID, String cardNumber, String cardType, Account account) {
        this.cardID = cardID;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.account = account;
    }

    public CreditCard(int cardID, String clientID, String cardNumber,
                      String cardType, Account account, List<Payment> payments) {
        this.cardID = cardID;
        this.clientID = clientID;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.account = account;
        this.payments = payments;
    }

    public CreditCard(int cardID, String clientID, String cardNumber,
                      String cardType, Account account) {
        this.cardID = cardID;
        this.clientID = clientID;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.account = account;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
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

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public Payment processPayment(CreditCard receiverCreditCard, double amount) {
        Payment payment = null;
        try {
            if (account.isBlocked() || receiverCreditCard.getAccount().isBlocked()) {
                throw new BlockedAccountException("Payment cannot be processed. Sender account is blocked.");
            }

            if (account.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient funds to make payment.");
            }

            if(account.getId() == receiverCreditCard.account.getId()) {
                throw new SimilarCardSenderReceiverException("Transactions" +
                        " between the same credit cards are not allowed.");
            }

            account.decreaseBalance(amount);
            receiverCreditCard.getAccount().increaseBalance(amount);
            payment = new Payment(account.getId(), receiverCreditCard.getAccount().getId(), amount);
            payments.add(payment);
            logger.info("Payment of " + amount + " processed successfully from account " + account.getId() +
                    " to account " + receiverCreditCard.getAccount().getId());
        } catch (BlockedAccountException | InsufficientFundsException e) {
            System.out.println(e.getMessage());
            logger.warn("Sender card number: " + cardNumber + "\t receiver card number: "
                    + receiverCreditCard.getCardNumber() + "\t" + e.getMessage());
        }
        return payment;
    }

    public static Map<String, Integer> getSortedCards(@NotNull List<Client> clients) {
        Map<String, Integer> cardTypesCount = new HashMap<>();
        for (Client client : clients) {
            for (CreditCard creditCard : client.getCreditCards()) {
                String cardType = creditCard.getCardType();
                cardTypesCount.put(cardType, cardTypesCount.getOrDefault(cardType, 0) + 1);
            }
        }

        return cardTypesCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    public static boolean isNewCardValid(List<Client> clients, @NotNull CreditCard newCreditCard) {
        if (newCreditCard.getAccount() == null) {
            logger.warn("Credit card: " + newCreditCard + " does not contain an instance of account.");
            return false;
        }

        if (newCreditCard.getAccount().getBalance() < 0 ) {
            logger.error("Cannot update credit card " + newCreditCard + ". There is a debt on the old account.");
            return false;
        }

        if(newCreditCard.getAccount().isBlocked()) {
            return false;
        }

        for (Client client : clients) {
                if (client.searchByCardNumber(newCreditCard.getCardNumber()) != null) {
                    logger.info("Card number already exists for another client.");
                    return false;
                }
        }
        return true;
    }

    public static boolean isOldCardValid(@NotNull CreditCard oldCreditCard, CreditCard updatedCreditCard) {
        if (oldCreditCard.getAccount() != null && oldCreditCard.getAccount().getBalance() > 0) {
            if (updatedCreditCard.getAccount() != null) {
                updatedCreditCard.getAccount().increaseBalance(oldCreditCard.getAccount().getBalance());
            } else {
                logger.error("Updated credit card " + updatedCreditCard +
                        " must contain an account to transfer the balance.");
                return false;
            }
        } else if (oldCreditCard.getAccount() != null && oldCreditCard.getAccount().getBalance() < 0) {
            logger.error("Cannot update credit card " + oldCreditCard + ". There is a debt on the old account.");
            return false;
        }

        logger.info("Old credit card "+ oldCreditCard + "can be updated by new credit card " + updatedCreditCard);
        return true;
    }

    public static boolean isCardAvailableToDelete(@NotNull CreditCard creditCardToDelete) {
        Account account = creditCardToDelete.getAccount();
        if (account.getBalance() != 0) {
            logger.warn("Can not close card. Balance is not zero.");
            return false;
        } else {
            return true;
        }
    }
}