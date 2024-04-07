package com.payments.restpayments.data.load;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static com.payments.restpayments.RestPaymentsApplication.*;


public class CardFileData {
    String cardFilePath;
    String paymentFilePath;
    BufferedReader buff;

    public CardFileData() {
    }

    public CardFileData(String cardFilePath, String paymentFilePath) {
        this.cardFilePath = cardFilePath;
        this.paymentFilePath = paymentFilePath;
    }

    // @Deprecated(forRemoval = true)
    public CardFileData(String cardFilePath) {
        this.cardFilePath = cardFilePath;
    }

    private String getFilePath(String fileType) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter " + fileType + " file path: ");
        return scanner.nextLine();
    }

    public void loadCreditCards() {
        if(cardFilePath == null) cardFilePath = getFilePath("credit cards");
        try {
            readCreditCardsData();
        }
        catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + cardFilePath + ANSI_RESET);
        }
    }

    private void readCreditCardsData() throws IOException {
        try {
            buff = new BufferedReader(new FileReader(cardFilePath));
            String creditCardData = buff.readLine();
            while(creditCardData != null) {
                String[] creditCardRawData = creditCardData.split(",");
                Account account = new Account(Integer.parseInt(creditCardRawData[4]),
                        Double.parseDouble(creditCardRawData[5]), Boolean.parseBoolean(creditCardRawData[6]));

                CreditCard creditCard = new CreditCard(Integer.parseInt(creditCardRawData[0]),
                        creditCardRawData[1], creditCardRawData[2],
                        creditCardRawData[3], account);

                Administrator.searchByID(clients, creditCard.getClientID()).addCreditCard(creditCard);
                creditCardData = buff.readLine();
            }
            buff.close();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + cardFilePath + ANSI_RESET);
        }
    }

    public void loadPayments() {
        if(paymentFilePath == null) paymentFilePath = getFilePath("transactions");
        try {
            readPaymentData();
        }
        catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + paymentFilePath + ANSI_RESET);
        }
    }

    private void readPaymentData() throws IOException {
        try {
            buff = new BufferedReader(new FileReader(paymentFilePath));
            String paymentData = buff.readLine();
            while(paymentData != null) {
                String[] paymentRawData = paymentData.split(",");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                // Parse the string into a LocalDateTime object
                LocalDateTime dateTime = LocalDateTime.parse(paymentRawData[2], formatter);
                Payment payment = new Payment(Integer.parseInt(paymentRawData[0]),
                        Double.parseDouble(paymentRawData[1]), dateTime,
                        Integer.parseInt(paymentRawData[3]), Integer.parseInt(paymentRawData[4]));


                CreditCard senderCreditCard = null;
                for(Client client : clients) {
                    for(CreditCard card : client.getCreditCards()) {
                        if(card.getAccount().getId() == payment.getSenderAccountID()) {
                            senderCreditCard = card;
                        }
                    }
                }

                CreditCard receiverCreditCard = null;
                for(Client client : clients) {
                    for(CreditCard card : client.getCreditCards()) {
                        if(card.getAccount().getId() == payment.getReceiverAccountID()) {
                            receiverCreditCard = card;
                        }
                    }
                }

                if ((senderCreditCard != null) && (receiverCreditCard != null)) {
                    senderCreditCard.getPayments().add(payment);
                    receiverCreditCard.getPayments().add(payment);
                } else {
                    System.out.println("Something went wrong!");
                }

                paymentData = buff.readLine();
            }
            buff.close();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "\n\t\tCan't open: " + paymentFilePath + ANSI_RESET);
        }
    }
}