package com.payments.restpayments.role;

import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String clientID;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private List<CreditCard> creditCards = new ArrayList<>(2);
    private static final Logger logger = LogManager.getLogger(Client.class);

    public Client() {
    }

    public Client(@NotNull Client client) {
        this.clientID = client.getClientID();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.creditCards = client.getCreditCards();
        this.username = client.getUsername();
        this.password = client.getPassword();
    }

    public Client(String clientID, String firstName,
                  String lastName, String username, String password) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public Client(String clientID, String firstName, String lastName,
                  String username, String password, List<CreditCard> creditCards) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.creditCards = creditCards;
    }

    @Deprecated(forRemoval = true)
    public Client(String clientID, String firstName, String lastName) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Deprecated(forRemoval = true)
    public Client(String clientID, String firstName,
                  String lastName, List<CreditCard> creditCards) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.creditCards = creditCards;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void addCreditCard(CreditCard creditCard) {
        creditCards.add(creditCard);
    }

    @Override
    public String toString() {
        return "Client {" +
                "clientID=" + clientID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", creditCards=" + creditCards +
                '}';
    }

    public void blockAccount(@NotNull Account account) {
        if (!account.isBlocked()) {
            account.setBlocked(true);
            System.out.println("Account " + account.getId() + " blocked successfully.");
        } else {
            System.out.println("Account is already blocked.");
        }
    }

    public void partiallyUpdate(@NotNull Client partialClient) {
        if (partialClient.getFirstName() != null) {
            this.setFirstName(partialClient.getFirstName());
        }
        if (partialClient.getLastName() != null) {
            this.setLastName(partialClient.getLastName());
        }
        if (partialClient.getPassword() != null) {
            this.setPassword(partialClient.getPassword());
        }
    }

    public CreditCard searchByCardNumber(String cardNumber) {
        for (CreditCard card : creditCards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }

    public boolean isValidNewClient(@NotNull List<Client> clients) {
        for (Client existingClient : clients) {
            if (this.clientID.equals(existingClient.getClientID()) &&
                    this.firstName.equals(existingClient.getFirstName()) &&
                    this.lastName.equals(existingClient.getLastName())) {
                logger.warn("User with ID '" + this.clientID +
                        "', first name '" + this.firstName +
                        "', and last name '" + this.lastName +
                        "' already exists.");
                return false;
            }

            if (!this.creditCards.isEmpty()) {
                for (CreditCard newCard : this.creditCards) {
                    for (CreditCard existingCard : existingClient.getCreditCards()) {
                        if (newCard.getCardNumber().equals(existingCard.getCardNumber()) &&
                                newCard.getCardType().equals(existingCard.getCardType())) {
                            logger.warn("User already has the same credit card: " + newCard.getCardNumber());
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}