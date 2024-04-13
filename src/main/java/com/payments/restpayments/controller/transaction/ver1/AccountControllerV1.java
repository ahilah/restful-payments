package com.payments.restpayments.controller.transaction.ver1;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
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
import java.util.ArrayList;
import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/api/v1/account", "/api/account/v1"})
@Tag(name = "API v1 Account", description = "Endpoints for Account API version 1")
public class AccountControllerV1 {
    private static final Logger logger = LogManager.getLogger(AccountControllerV1.class);

    // http://localhost:8080/api/v1/account
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all accounts by client",
            description = "Retrieve a list of all accounts associated with the specified client")
    @GetMapping("")
    @ResponseBody
    public List<Account> getAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Client client = null;
        List<Account> accounts = new ArrayList<>();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /account/v1 was called by " + userDetails + LocalDateTime.now());
            for (CreditCard creditCard : client.getCreditCards()) {
                accounts.add(creditCard.getAccount());
            }
        } else {
            logger.warn("Client not found or does not exist" + "\t" + userDetails + "\t" + LocalDateTime.now());
        }
        return accounts;
    }
}