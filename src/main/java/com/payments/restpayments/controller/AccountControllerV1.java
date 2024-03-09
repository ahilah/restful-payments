package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/account/v1", "/v1/account"})
public class AccountControllerV1 {
    List<Client> clientsLocal = clients;

    // http://localhost:8080/v1/account/
    @GetMapping("/{clientId}")
    @ResponseBody
    public List<Account> getAccounts(@PathVariable int clientId) {
        Client client = Administrator.searchByID(clientsLocal, clientId);
        List<Account> accounts = new ArrayList<>();
        for (CreditCard creditCard : client.getCreditCards()) {
            accounts.add(creditCard.getAccount());
        }
        return accounts;
    }
}
