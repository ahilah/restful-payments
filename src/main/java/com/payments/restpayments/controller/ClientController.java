package com.payments.restpayments.controller;

import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping("/client")
public class ClientController {
    List<Client> clientsLocal = clients;

    public ClientController() {
    }

    // http://localhost:8080/client/get/
    @GetMapping("/get/{clientId}")
    @ResponseBody
    public Client getClientById(@PathVariable int clientId) {
        int realId = clientId - 1;
        return clientsLocal.get(realId);
    }

    // http://localhost:8080/client/get/card/
    @GetMapping("/get/card/{creditCardId}")
    @ResponseBody
    public CreditCard getClientCreditCard(@PathVariable String creditCardId) {
        for(Client client : clientsLocal) {
            for(CreditCard card : client.getCreditCards()) {
                if(card.getCardNumber().equals(creditCardId)) {
                    return card;
                }
            }
        }
        return null;
    }

    // http://localhost:8080/client/update/
    @PutMapping("/update/{clientId}")
    @ResponseBody
    public Client updateClient(@PathVariable int clientId, @RequestBody Client client) {
        int realId = clientId - 1;
        clientsLocal.set(realId, client);
        return clientsLocal.get(realId);
    }

    // http://localhost:8080/client/update/part/
    @PatchMapping("/update/part/{clientId}")
    public Client partiallyUpdateClient(@PathVariable int clientId, @RequestBody Client partialClient) {
        int realId = clientId - 1;
        Client existingClient = clientsLocal.get(realId);

        if (existingClient == null) {
            return null;
        }
        existingClient.partiallyUpdate(partialClient);
        return existingClient;
    }


    // http://localhost:8080/client/block
    @PatchMapping("/block/{clientId}/{accountId}")
    public Client updateClientBlockedStatus(@PathVariable int clientId, @PathVariable int accountId) {
        int realId = clientId - 1;
        Client client = clientsLocal.get(realId);
        if (client != null) {
            List<CreditCard> creditCards = client.getCreditCards();
            if (!creditCards.isEmpty()) {
                for(CreditCard creditCard : creditCards) {
                    if (creditCard.getAccount().getId() == accountId)
                        client.blockAccount(creditCard.getAccount());
                }
            } else {
                System.out.println("No credit cards associated with this client.");
            }
        } else {
            System.out.println("Client with ID " + clientId + " not found.");
        }
        return client;
    }

    // http://localhost:8080/client/payment/
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

}
/* get available credit cards type
get false/true credit cards
* delete credit card by its ID
* */