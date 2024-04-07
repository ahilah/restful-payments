package com.payments.restpayments.role;

import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Administrator {
    private String adminID;
    private String username;
    private String password;
    private static final Logger logger = LogManager.getLogger(Administrator.class);

    public Administrator() {
    }

    public Administrator(Administrator admin) {
        this.adminID = admin.getAdminID();
        this.username = admin.getUsername();
        this.password = admin.getPassword();
    }

    public Administrator(String adminID, String username, String password) {
        this.adminID = adminID;
        this.username = username;
        this.password = password;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
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

    public void removeAccountBlock(@NotNull Account account) {
        if (account.isBlocked()) {
            account.setBlocked(false);
            System.out.println("Account " + account.getId() + " unblocked successfully by admin ID = " + adminID);
        } else {
            System.out.println("Account is not blocked.");
        }
    }

    public static <T> T searchByID(@NotNull List<T> entities, String entityId) {
        Optional<T> entityOptional = entities.stream()
                .filter(entity -> (entity instanceof Client && Objects.equals(((Client) entity).getClientID(), entityId))
                        || (entity instanceof Administrator && Objects.equals(((Administrator) entity).getAdminID(), entityId)))
                .findFirst();
        return entityOptional.orElse(null);
    }

    public static <T> T searchByUsername(@NotNull List<T> entities, String entityUsername) {
        Optional<T> entityOptional = entities.stream()
                .filter(entity -> (entity instanceof Client &&
                        Objects.equals(((Client) entity).getUsername(), entityUsername))
                        || (entity instanceof Administrator &&
                        Objects.equals(((Administrator) entity).getUsername(), entityUsername)))
                .findFirst();
        return entityOptional.orElse(null);
    }

    public static <T> T searchByNumber(@NotNull List<T> entities, String entityId) {
        Optional<T> entityOptional = entities.stream()
                .filter(entity -> (entity instanceof CreditCard &&
                        ((CreditCard) entity).getCardNumber().equals(entityId)))
                .findFirst();
        return entityOptional.orElse(null);
    }

    public Administrator partiallyUpdate(@NotNull Administrator partialAdmin) {
        if (partialAdmin.getUsername() != null) {
            this.setUsername(partialAdmin.getUsername());
        }
        if (partialAdmin.getPassword() != null) {
            this.setPassword(partialAdmin.getPassword());
        }
        return this;
    }

    public static @NotNull Set<Payment> showPaymentsInfo(@NotNull List<Client> clients) {
        Set<Payment> payments = new HashSet<>();
        for(Client client : clients) {
            for(CreditCard creditCard : client.getCreditCards()) {
                payments.addAll(creditCard.getPayments());
            }
        }
        return payments;
    }

    public boolean isValidNewAdmin(@NotNull List<Administrator> admins) {
        for (Administrator admin : admins) {
            if (this.getUsername().equals(admin.getUsername())
                    && this.getPassword().equals(admin.getPassword())) {
                logger.warn("Admin has already exists");
                return false;
            }
        }
        return true;
    }
}