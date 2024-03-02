package com.payments.restpayments.role;

import com.payments.restpayments.transaction.Account;

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

}
