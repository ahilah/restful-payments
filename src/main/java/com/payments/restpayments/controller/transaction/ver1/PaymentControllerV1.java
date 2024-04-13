package com.payments.restpayments.controller.transaction.ver1;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/api/payment/v1", "/api/v1/payment"})
@Tag(name = "API v1 Payment", description = "Endpoints for Payment API version 1")
public class PaymentControllerV1 {
    Set<Payment> payments = Administrator.showPaymentsInfo(clients);
    private static final Logger logger = LogManager.getLogger(PaymentControllerV1.class);
    public PaymentControllerV1() {
    }

    // http://localhost:8080/api/v1/payment/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Payments", description = "Retrieve a list of all payments")
    @GetMapping("/")
    @ResponseBody
    public Set<Payment> getAllPayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            payments = Administrator.showPaymentsInfo(clients);
            logger.info("Endpoint /payment/v1/ was called by admin" + userDetails + LocalDateTime.now());
        } else {
            logger.warn("Admin was not found or does not exist\t" + userDetails + "\t" + LocalDateTime.now());
            return null;
        }
        return payments;
    }
}