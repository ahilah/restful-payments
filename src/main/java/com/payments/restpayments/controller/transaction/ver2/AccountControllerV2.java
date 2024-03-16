package com.payments.restpayments.controller.transaction.ver2;

import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/account/v2", "/v2/account"})
public class AccountControllerV2 {
    List<Client> clientsLocal = clients;

    // http://localhost:8080/v2/account/
    @GetMapping("/{cardNumber}")
    @ResponseBody
    public Account getAccount(@PathVariable String cardNumber) {
        CreditCard findCreditCard = null;

        for(Client client : clientsLocal) {
            for(CreditCard creditCard : client.getCreditCards()) {
                if(creditCard.getCardNumber().equals(cardNumber)) {
                    findCreditCard = creditCard;
                    break;
                }
            }
            if(findCreditCard != null) {
                break;
            }
        }
        if(findCreditCard == null) {
            System.out.println("Card not found.");
            return null;
        }

        return findCreditCard.getAccount();
    }
}