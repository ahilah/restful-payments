package com.payments.restpayments.controller.transaction.ver2;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/api/v2/account", "/api/account/v2"})
@Tag(name = "API v2 Account ", description = "Endpoints for Account API version 2")
public class AccountControllerV2 {
    private static final Logger logger = LogManager.getLogger(AccountControllerV2.class);

    // http://localhost:8080/api/v2/account/
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get Account by Card Number",
            description = "Retrieve account information based on the provided card number")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the account information",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Account.class)))
    @Parameter(name = "cardNumber", description = "The card number associated with the client and account",
            required = true, example = "691")
    @GetMapping("/{cardNumber}")
    @ResponseBody
    public Account getAccount(@PathVariable String cardNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Client client = null;
        CreditCard creditCard = null;
        Account account = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /v2/account/ was called by " + userDetails + LocalDateTime.now());
            creditCard = client.searchByCardNumber(cardNumber);
            if(creditCard == null) {
                logger.warn("Credit card with number " + cardNumber + " not found.");
                return null;
            }
            account = client.searchByCardNumber(cardNumber).getAccount();
        } else {
            logger.warn("Client was not found or does not exist\t" + userDetails + "\t" + LocalDateTime.now());
        }
        return account;
    }
}