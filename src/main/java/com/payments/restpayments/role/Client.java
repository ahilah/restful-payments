package com.payments.restpayments.role;

import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

public class Client {
    private int id;
    private String firstName;
    private String lastName;
    private List<CreditCard> creditCards = new ArrayList<>(2);

    public Client() {
    }

    public Client(Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.creditCards = client.getCreditCards();
    }

    public Client(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Client(int id, String firstName, String lastName, List<CreditCard> creditCards) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.creditCards = creditCards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void addCreditCard(CreditCard creditCard) {
        creditCards.add(creditCard);
    }

    @Override
    public String toString() {
        return "Client {" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", creditCards=" + creditCards +
                '}';
    }

    public void blockAccount(Account account) {
        if (!account.isBlocked()) {
            account.setBlocked(true);
            System.out.println("Account " + account.getId() + " blocked successfully.");
        } else {
            System.out.println("Account is already blocked.");
        }
    }

    public void partiallyUpdate(Client partialClient) {
        if (partialClient.getFirstName() != null) {
            this.setFirstName(partialClient.getFirstName());
        }
        if (partialClient.getLastName() != null) {
            this.setLastName(partialClient.getLastName());
        }
        if (partialClient.getCreditCards() != null) {
            // this.creditCards.clear(); // Очищаємо поточний список кредитних карток клієнта
            this.creditCards.addAll(partialClient.getCreditCards()); // Додаємо нові кредитні картки
        }
    }

    public CreditCard searchByCardNumber(String cardNumber) {
        for (CreditCard card : creditCards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card; // Повертаємо кредитну картку, якщо знайдено збіг
            }
        }
        return null; // Повертаємо null, якщо кредитна картка не знайдена
    }
}