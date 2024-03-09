package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.payments.restpayments.RestPaymentsApplication.clients;


@RestController
@RequestMapping({"/card/v2", "/v2/card"})
public class CreditCardControllerV2 {
    List<Client> clientsLocal = clients;
    Map<String, Integer> sortedCardTypesCount = CreditCard.getSortedCards(clientsLocal);;

    public CreditCardControllerV2() {
    }

    // http://localhost:8080/v2/card/
    @GetMapping("/")
    @ResponseBody
    public Map<String, Integer> getCardTypesCount() {
        return sortedCardTypesCount;
    }

    // http://localhost:8080/v2/card/
    @GetMapping("/{cardType}")
    @ResponseBody
    public String getCardTypeCount(@PathVariable String cardType) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sortedCardTypesCount.entrySet()) {
            if(entry.getKey().equals(cardType)) {
                result.append("Card Type: ")
                        .append(entry.getKey())
                        .append(", Count: ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }
        return result.toString();
    }

    // http://localhost:8080/card/v2/update/
    @PutMapping("/update/{cardNumber}")
    @ResponseBody
    public String updateCreditCard(@PathVariable String cardNumber,
                                   @RequestBody CreditCard updatedCreditCard) {

        CreditCard oldCreditCard = null;
        Client clnt = null;

        for(Client client : clientsLocal) {
            for(CreditCard creditCard : client.getCreditCards()) {
                if(creditCard.getCardNumber().equals(cardNumber)) {
                    clnt = client;
                    oldCreditCard = creditCard;
                    break;
                }
            }
            if(oldCreditCard != null) {
                break;
            }
        }
        if(oldCreditCard == null) {
            System.out.println("Card not found.");
            return "Card not found.";
        }

        Account account = oldCreditCard.getAccount();
        if(account.getBalance() != 0) {
            System.out.println("Can not close card. Balance is not zero.");
            return "Can not close card. Balance is not zero.";
        } else {
            clnt.blockAccount(account);
            System.out.println("Account successfully blocked.");
        }

        CreditCard creditCard = new CreditCard(updatedCreditCard);
        clnt.getCreditCards().set(clnt.getCreditCards().indexOf(oldCreditCard), creditCard);
        return "Credit Card with Number " + cardNumber + " was successfully updated.";
    }


    // http://localhost:8080/card/v2/bulkAdd
    @PostMapping("/bulkAdd/{clientId}")
    @ResponseBody
    public String bulkAddCreditCards(@PathVariable int clientId,
                                 @RequestBody List<CreditCard> newCreditCards) {
        Client client = Administrator.searchByID(clientsLocal, clientId);
        client.getCreditCards().addAll(newCreditCards);
        return "Credit Cards were successfully added in bulk.";
    }

    // http://localhost:8080/client/v2/
    @DeleteMapping("/{clientId}/{cardNumber}")
    public String deleteCard(@PathVariable int clientId,
                             @PathVariable String cardNumber) {
        CreditCard creditCardToDelete = null;

        Client client = Administrator.searchByID(clientsLocal, clientId);
            for(CreditCard creditCard : client.getCreditCards()) {
                if(creditCard.getCardNumber().equals(cardNumber)) {
                    creditCardToDelete = creditCard;
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
            client.blockAccount(account);
            System.out.println("Account successfully blocked.");
        }

        client.getCreditCards().remove(creditCardToDelete);
        return "Credit Card was successfully closed and deleted.";
    }
}