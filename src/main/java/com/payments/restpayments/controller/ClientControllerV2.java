package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/client/v2", "/v2/client"})
public class ClientControllerV2 {
    List<Client> clientsLocal = clients;

    public ClientControllerV2() {
    }

    // http://localhost:8080/client/v2/
    @GetMapping("/{clientId}")
    @ResponseBody
    public Set<CreditCard> getAvailableCreditCards(@PathVariable int clientId) {
        Client existingClient = Administrator.searchByID(clientsLocal, clientId);
        Set<CreditCard> creditCards = new HashSet<>();
        for(CreditCard creditCard : existingClient.getCreditCards()) {
            if(!creditCard.getAccount().isBlocked()) {
                creditCards.add(creditCard);
            }
        }

        return creditCards;
    }

    // http://localhost:8080/client/v2/
    @GetMapping("/blocked/{clientId}")
    @ResponseBody
    public Set<CreditCard> getBlockedCreditCards(@PathVariable int clientId) {
        Client existingClient = Administrator.searchByID(clientsLocal, clientId);
        Set<CreditCard> creditCards = new HashSet<>();
        for(CreditCard creditCard : existingClient.getCreditCards()) {
            if(creditCard.getAccount().isBlocked()) {
                creditCards.add(creditCard);
            }
        }

        return creditCards;
    }

    // http://localhost:8080/client/v1/update/
    @PutMapping("/update/{clientId}")
    @ResponseBody
    public Client updateClient(@PathVariable int clientId, @RequestBody Client client) {
        Client clientOptional = Administrator.searchByID(clientsLocal, clientId);
        clientsLocal.set(clientsLocal.indexOf(clientOptional), client);
        return clientsLocal.get(clientsLocal.indexOf(client));
    }

    // http://localhost:8080/client/v2/payment/
    @PatchMapping("/payment/{senderCreditCardId}/{receiverCreditCardId}/{amount}")
    public Payment makePaymentTransaction(@PathVariable String senderCreditCardId,
                                         @PathVariable String receiverCreditCardId,
                                         @PathVariable double amount) {
        CreditCard senderCreditCard = null;
        for(Client client : clientsLocal) {
            for(CreditCard card : client.getCreditCards()) {
                if(card.getCardNumber().equals(senderCreditCardId)) {
                    senderCreditCard = card;
                }
            }
        }

        CreditCard receiverCreditCard = null;
        for(Client client : clientsLocal) {
            for(CreditCard card : client.getCreditCards()) {
                if(card.getCardNumber().equals(receiverCreditCardId)) {
                    receiverCreditCard = card;
                }
            }
        }

        Payment payment = null;
        if ((senderCreditCard != null) && (receiverCreditCard != null)) {
            payment = senderCreditCard.processPayment(receiverCreditCard, amount);
            receiverCreditCard.getPayments().add(payment);
        } else {
            System.out.println("Client with Credit Card ID " + senderCreditCardId + " not found.");
        }
        return payment;
    }

    // http://localhost:8080/client/v2/
    @DeleteMapping("/{cardNumber}")
    public String deleteCard(@PathVariable String cardNumber) {
        CreditCard creditCardToDelete = null;
        Client clnt = null;

        for(Client client : clientsLocal) {
            for(CreditCard creditCard : client.getCreditCards()) {
                if(creditCard.getCardNumber().equals(cardNumber)) {
                    clnt = client;
                    creditCardToDelete = creditCard;
                    break;
                }
            }
            if(creditCardToDelete != null) {
                break;
            }
        }
        if(creditCardToDelete == null) {
            System.out.println("Card not found.");
            return "Card not found.";
        }

        Account account = creditCardToDelete.getAccount();
        if(account.getBalance() != 0) {
            System.out.println("Can not close card. Balance is not zero.");
            return "Can not close card. Balance is not zero.";
        } else {
            clnt.blockAccount(account);
            System.out.println("Account successfully blocked.");
        }

        clnt.getCreditCards().remove(creditCardToDelete);
        return "Credit Card was successfully closed and deleted.";
    }

}