package com.payments.restpayments.controller.transaction.ver2;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/api/v2/payment", "/api/payment/v2"})
@Tag(name = "API v2 Payment", description = "Endpoints for Payment API version 2")
public class PaymentControllerV2 {
    Set<Payment> payments = Administrator.showPaymentsInfo(clients);
    private static final Logger logger = LogManager.getLogger(PaymentControllerV2.class);
    public PaymentControllerV2() {
    }

    // http://localhost:8080/api/v2/payment/
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get Client Payments",
            description = "Retrieve payments associated with the provided card number")
    @GetMapping("/{cardNumber}")
    @ResponseBody
    public ResponseEntity<?> getClientPayments(@PathVariable String cardNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        logger.info("Endpoint /payment/v2/ was called by " + userDetails + LocalDateTime.now());

        Client client = null;
        CreditCard creditCard = null;
        Set<Payment> clientPayments = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            client = Administrator.searchByUsername(clients, userDetails.getUsername());
            payments = Administrator.showPaymentsInfo(clients);
            creditCard = client.searchByCardNumber(cardNumber);

            int accountId = creditCard.getAccount().getId(),
                    senderId, receiverId;
            clientPayments = new HashSet<>();
            for(Payment payment : payments) {
                senderId = payment.getSenderAccountID();
                receiverId = payment.getReceiverAccountID();
                if(senderId == accountId || receiverId == accountId) {
                    clientPayments.add(payment);
                }
            }
        } else {
            logger.warn("Client was not found or does not exist\t" + userDetails + "\t" + LocalDateTime.now());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(clientPayments);
    }
}