package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/payment/v1", "/v1/payment"})
public class PaymentControllerV1 {
    List<Client> clientsLocal = clients;
    List<Payment> payments = Administrator.showPaymentsInfo(clientsLocal);
    public PaymentControllerV1() {
    }

    // http://localhost:8080/payment/v1/
    @GetMapping("/")
    @ResponseBody
    public List<Payment> getAllPayment() {
        payments = Administrator.showPaymentsInfo(clientsLocal);
        return payments;
    }
}
