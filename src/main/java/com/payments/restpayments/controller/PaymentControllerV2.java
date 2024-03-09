package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/payment/v2", "/v2/payment"})
public class PaymentControllerV2 {
    List<Client> clientsLocal = clients;
    List<Payment> payments = Administrator.showPaymentsInfo(clientsLocal);
    public PaymentControllerV2() {
    }

    // http://localhost:8080/payment/v2/
    @GetMapping("/{cardNumber}")
    @ResponseBody
    public Set<Payment> getClientPayments(@PathVariable String cardNumber) {
        payments = Administrator.showPaymentsInfo(clientsLocal);
        List<CreditCard> creditCards = new ArrayList<>();
        for(Client client : clientsLocal) {
            creditCards.addAll(client.getCreditCards());
        }
        CreditCard creditCard = Administrator.searchByNumber(creditCards, cardNumber);
        int accountId = creditCard.getAccount().getId(),
                senderId, receiverId;
        Set<Payment> clientPayments = new HashSet<>();
        for(Payment payment : payments) {
            senderId = payment.getSenderAccountID();
            receiverId = payment.getReceiverAccountID();
            if(senderId == accountId || receiverId == accountId) {
                clientPayments.add(payment);
            }
        }
        return clientPayments;
    }
}
