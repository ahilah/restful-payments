package com.payments.restpayments.role;

import java.util.Random;

public class SuperAdmin {
    private static SuperAdmin instance;

    private Random random = new Random();
    private int id = random.nextInt(1000);
    private String username = "bossa";
    private String password = "nova";

    private SuperAdmin() {
    }

    public static synchronized SuperAdmin getInstance() {
        if (instance == null) {
            instance = new SuperAdmin();
        }
        return instance;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}