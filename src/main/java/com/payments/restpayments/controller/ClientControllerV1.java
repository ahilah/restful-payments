package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/client/v1", "/v1/client"})
public class ClientControllerV1 {
    List<Client> clientsLocal = clients;

    public ClientControllerV1() {
    }

    // http://localhost:8080/client/v1/
    @GetMapping("/{clientId}")
    @ResponseBody
    public Client getClientById(@PathVariable int clientId) {
        Client existingClient = Administrator.searchByID(clientsLocal, clientId);

        if (existingClient == null) {
            System.out.println(123);
        }
        return existingClient;
    }

    // http://localhost:8080/client/v1/card/
    @GetMapping("/card/{creditCardId}")
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

    // http://localhost:8080/client/v1/update/
    @PutMapping("/update/{clientId}")
    @ResponseBody
    public Client updateClient(@PathVariable int clientId, @RequestBody Client client) {
        Client clientOptional = Administrator.searchByID(clientsLocal, clientId);
        clientsLocal.set(clientsLocal.indexOf(clientOptional), client);
        return clientsLocal.get(clientsLocal.indexOf(client));
    }

    // http://localhost:8080/client/v1/update/part/
    @PatchMapping("/update/part/{clientId}")
    public Client partiallyUpdateClient(@PathVariable int clientId, @RequestBody Client partialClient) {
        Client existingClient = Administrator.searchByID(clientsLocal, clientId);

        if (existingClient == null) {
            return null;
        }
        existingClient.partiallyUpdate(partialClient);
        return existingClient;
    }


    // http://localhost:8080/client/v1/block
    @PatchMapping("/block/{clientId}/{accountId}")
    public Client updateClientBlockedStatus(@PathVariable int clientId, @PathVariable int accountId) {
        Client client = Administrator.searchByID(clientsLocal, clientId);
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

    // http://localhost:8080/client/v1/
    @DeleteMapping("/{clientId}")
    public String deleteCards(@PathVariable int clientId) {
        Client client = Administrator.searchByID(clientsLocal, clientId);
        List<CreditCard> creditCards = client.getCreditCards();
        for(CreditCard creditCard : creditCards) {
            Account account = creditCard.getAccount();
            if(account.getBalance() != 0) {
                System.out.println("Can not close cards.");
                return "Can not close cards.";
            } else {
                client.blockAccount(account);
                System.out.println("Account successfully blocked.");
            }
        }
        creditCards.clear();
        return "Credit Card were successfully closed and deleted.";
    }
}