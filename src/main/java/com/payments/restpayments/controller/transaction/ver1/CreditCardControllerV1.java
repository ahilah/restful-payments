package com.payments.restpayments.controller.transaction.ver1;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.CreditCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;


@RestController
@RequestMapping({"/card/v1", "/v1/card"})
@Tag(name = "Credit Card API v1", description = "Endpoints for Credit Card API version 1")
public class CreditCardControllerV1 {
    private static final Logger logger = LogManager.getLogger(CreditCardControllerV1.class);

    public CreditCardControllerV1() {
    }

    // http://localhost:8080/card/v1/
    @Operation(summary = "Get Card Types", description = "Retrieve a set of unique card types")
    @GetMapping("/")
    @ResponseBody
    public Set<String> getCardTypes() {
        Set<String> cardTypes = new HashSet<>();
        for(Client client : clients) {
            for(CreditCard creditCard : client.getCreditCards()) {
                cardTypes.add(creditCard.getCardType());
            }
        }
        return cardTypes;
    }

    // http://localhost:8080/card/v1/update/
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Full Update Credit Card", description = "Update an existing credit card")
    @PutMapping("/update/{cardNumber}")
    @ResponseBody
    public List<CreditCard> updateCreditCard(@PathVariable String cardNumber,
                                             @RequestBody CreditCard updatedCreditCard) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Client client = null;
        CreditCard oldCreditCard = null,
                newCreditCard = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /card/v1/update/ was called by " + userDetails + LocalDateTime.now());
            oldCreditCard = client.searchByCardNumber(cardNumber);
            newCreditCard = new CreditCard(updatedCreditCard);

            if (CreditCard.isNewCardValid(clients, newCreditCard) &&
                    CreditCard.isOldCardValid(oldCreditCard, newCreditCard)) {
                client.getCreditCards().set(client.getCreditCards().indexOf(oldCreditCard), newCreditCard);
                logger.info("Credit card \t" + updatedCreditCard +
                        "was successfully updated by client: \t" +userDetails);
                if(oldCreditCard.getAccount().getId() != newCreditCard.getAccount().getId()) {
                    client.blockAccount(oldCreditCard.getAccount());
                    logger.info("Old account \t" + oldCreditCard.getAccount() +
                            "was successfully updated by client: \t" + userDetails);
                } else logger.info("Old credit card: " + oldCreditCard +
                        " and new credit card: " + newCreditCard +
                        " have the same account: " + newCreditCard.getAccount().getId());
            }
        } else {
            logger.warn("Client not found or does not exist\t" + userDetails + "\t" + LocalDateTime.now());
        }
        return Objects.requireNonNull(client).getCreditCards();
    }

    // http://localhost:8080/card/v1/add
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create Credit Card", description = "Create a new credit card for a client")
    @PostMapping("/add")
    @ResponseBody
    public List<CreditCard> createCreditCard(@RequestBody CreditCard creditCard) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Client client = null;
        CreditCard newCreditCard = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /card/v1/add was called by " + userDetails + LocalDateTime.now());
            newCreditCard = new CreditCard(creditCard);
            if(CreditCard.isNewCardValid(clients, newCreditCard)) {
                client.getCreditCards().add(newCreditCard);
                logger.info("New credit card "+ newCreditCard + " was successfully added");
            }
        } else {
            logger.warn("Client not found or does not exist\t" + userDetails + "\t" + LocalDateTime.now());
            return null;
        }

        return client.getCreditCards();
    }
}