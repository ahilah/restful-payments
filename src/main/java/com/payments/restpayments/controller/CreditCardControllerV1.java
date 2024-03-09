package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;


@RestController
@RequestMapping({"/card/v1", "/v1/card"})
public class CreditCardControllerV1 {
    List<Client> clientsLocal = clients;

    public CreditCardControllerV1() {
    }

    // http://localhost:8080/card/v1/
    @GetMapping("/")
    @ResponseBody
    public Set<String> getCardTypes() {
        Set<String> cardTypes = new HashSet<>();
        for(Client client : clientsLocal) {
            for(CreditCard creditCard : client.getCreditCards()) {
                cardTypes.add(creditCard.getCardType());
            }
        }
        return cardTypes;
    }

    // http://localhost:8080/card/v1/update/
    @PutMapping("/update/{clientId}/{cardNumber}")
    @ResponseBody
    public String updateCreditCard(@PathVariable int clientId,
                                   @PathVariable String cardNumber,
                                   @RequestBody CreditCard updatedCreditCard) {
        Client client = Administrator.searchByID(clientsLocal, clientId);
        CreditCard oldCreditCard = client.searchByCardNumber(cardNumber);
        CreditCard creditCard = new CreditCard(updatedCreditCard);
        client.getCreditCards().set(client.getCreditCards().indexOf(oldCreditCard), creditCard);
        return "Credit Card with Number " + cardNumber + " was successfully updated.";
    }

    // http://localhost:8080/card/v1/add/
    @PostMapping("/add/{clientId}")
    @ResponseBody
    public String createCreditCard(@PathVariable int clientId,
                               @RequestBody CreditCard creditCard) {
        Client client = Administrator.searchByID(clientsLocal, clientId);
        client.getCreditCards().add(new CreditCard(creditCard));
        return "Client is successfully added.";
    }


}