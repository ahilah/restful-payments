package com.payments.restpayments.data.load;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static com.payments.restpayments.RestPaymentsApplication.*;

public class RoleFileData {
    String userFilePath;
    String adminFilePath;
    BufferedReader buff;

    public RoleFileData() {
    }

    public RoleFileData(String userFilePath, String adminFilePath) {
        this.userFilePath = userFilePath;
        this.adminFilePath = adminFilePath;
    }

    @Deprecated(forRemoval = true)
    public RoleFileData(String adminFilePath) {
        this.adminFilePath = adminFilePath;
    }

    private String getFilePath(String role) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter " + role + " file path: ");
        return scanner.nextLine();
    }

    public void getAdminData() {
        if(adminFilePath == null) adminFilePath = getFilePath("admin");
        try {
            readAdminFile();
        }
        catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + adminFilePath + ANSI_RESET);
        }
    }

    private void readAdminFile() throws IOException {
            try {
                buff = new BufferedReader(new FileReader(adminFilePath));
                String adminData = buff.readLine();
                while(adminData != null) {
                    String[] adminRawData = adminData.split(",");
                    Administrator admin = new Administrator(adminRawData[0],
                            adminRawData[1], adminRawData[2]);
                    admins.add(admin);
                    adminData = buff.readLine();
                }
                buff.close();
            } catch (IOException e) {
                System.out.println(ANSI_RED + "\n\t\tCan't open: " + adminFilePath + ANSI_RESET);
        }
    }

    public void getUserData() {
        if(userFilePath == null) userFilePath = getFilePath("client");
        try {
            readUserFile();
        }
        catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + userFilePath + ANSI_RESET);
        }
    }

    private void readUserFile() throws IOException {
        try {
            buff = new BufferedReader(new FileReader(userFilePath));
            String userData = buff.readLine();
            while(userData != null) {
                String[] userRawData = userData.split(",");
                Client client = new Client(userRawData[0],
                        userRawData[1], userRawData[2],
                        userRawData[3], userRawData[4]);
                clients.add(client);
                userData = buff.readLine();
            }
            buff.close();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + userFilePath + ANSI_RESET);
        }
    }
}