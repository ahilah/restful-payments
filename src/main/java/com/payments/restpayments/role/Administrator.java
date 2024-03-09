package com.payments.restpayments.role;

import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Administrator {
    private int id;
    private String username;
    private String password;

    public Administrator() {
    }

    public Administrator(Administrator admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.password = admin.getPassword();
    }

    public Administrator(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void removeAccountBlock(Account account) {
        if (account.isBlocked()) {
            account.setBlocked(false);
            System.out.println("Account " + account.getId() + " unblocked successfully by admin ID = " + id);
        } else {
            System.out.println("Account is not blocked.");
        }
    }

    public static <T> T searchByID(List<T> entities, int entityId) {
        Optional<T> entityOptional = entities.stream()
                .filter(entity -> (entity instanceof Client && ((Client) entity).getId() == entityId)
                        || (entity instanceof Administrator && ((Administrator) entity).getId() == entityId))
                .findFirst();
        return entityOptional.orElse(null);
    }

    public static <T> T searchByNumber(List<T> entities, String entityId) {
        Optional<T> entityOptional = entities.stream()
                .filter(entity -> (entity instanceof CreditCard &&
                        ((CreditCard) entity).getCardNumber().equals(entityId)))
                .findFirst();
        return entityOptional.orElse(null);
    }

    public Administrator partiallyUpdate(Administrator partialAdmin) {
        if (partialAdmin.getUsername() != null) {
            this.setUsername(partialAdmin.getUsername());
        }
        if (partialAdmin.getPassword() != null) {
            this.setPassword(partialAdmin.getPassword());
        }
        return this;
    }

    public static List<Payment> showPaymentsInfo(List<Client> clients) {
        List<Payment> payments = new ArrayList<>();
        for(Client client : clients) {
            for(CreditCard creditCard : client.getCreditCards()) {
                payments.addAll(creditCard.getPayments());
            }
        }
        return payments;
    }
}