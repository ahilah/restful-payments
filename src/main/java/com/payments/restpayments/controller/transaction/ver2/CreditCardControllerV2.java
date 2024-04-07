package com.payments.restpayments.controller.transaction.ver2;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.CreditCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.payments.restpayments.RestPaymentsApplication.clients;


@RestController
@RequestMapping({"/card/v2", "/v2/card"})
@Tag(name = "Credit Card API v2", description = "Endpoints for Credit Card API version 2")
public class CreditCardControllerV2 {
    Map<String, Integer> sortedCardTypesCount = CreditCard.getSortedCards(clients);
    private static final Logger logger = LogManager.getLogger(CreditCardControllerV2.class);

    public CreditCardControllerV2() {
    }

    // http://localhost:8080/v2/card
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Card Types Count",
            description = "Retrieve a map containing the count of each card type")
    @GetMapping("")
    @ResponseBody
    public Map<String, Integer> getCardTypesCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested credit cards metadata");
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
        return sortedCardTypesCount;
    }

    // http://localhost:8080/v2/card/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Card Type Count",
            description = "Retrieve the count of a specific card type")
    @GetMapping("/{cardType}")
    @ResponseBody
    public String getCardTypeCount(@PathVariable String cardType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        StringBuilder result = new StringBuilder();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested credit cards metadata");

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
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/card/v2/update/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Full Update Credit Card", description = "Update an existing credit card")
    @PutMapping("/update/{cardNumber}")
    @ResponseBody
    public String updateCreditCard(@PathVariable String cardNumber,
                                   @RequestBody CreditCard updatedCreditCard) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreditCard oldCreditCard = null;
        Client clnt = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client data");

            for(Client client : clients) {
                CreditCard creditCard = client.searchByCardNumber(cardNumber);
                if(creditCard != null) {
                    clnt = client;
                    oldCreditCard = creditCard;
                    break;
                }
            }

            if(oldCreditCard == null) {
                logger.warn("Card not found.");
                return "Card not found";
            }

            CreditCard newCreditCard = new CreditCard(updatedCreditCard);
            if (CreditCard.isNewCardValid(clients, newCreditCard) &&
                    CreditCard.isOldCardValid(oldCreditCard, newCreditCard)) {
                clnt.getCreditCards().set(clnt.getCreditCards().indexOf(oldCreditCard), newCreditCard);
                logger.info("Credit card \t" + updatedCreditCard +
                        "was successfully updated by admin: \t" + userDetails);
            }

            if(oldCreditCard.getAccount().getId() != newCreditCard.getAccount().getId()) {
                clnt.blockAccount(oldCreditCard.getAccount());
                logger.info("Old account \t" + oldCreditCard.getAccount() +
                        "was successfully updated by client: \t" +userDetails);
            } else logger.info("Old credit card: " + oldCreditCard +
                    " and new credit card: " + newCreditCard +
                    " have the same account: " + newCreditCard.getAccount().getId());
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
        return "Credit Card with Number " + cardNumber + " was successfully updated.";
    }

    // http://localhost:8080/card/v2/bulkAdd/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Bulk Add Credit Cards",
            description = "Bulk add new credit cards to a client's account")
    @PostMapping("/bulkAdd/{clientId}")
    @ResponseBody
    public List<CreditCard> bulkAddCreditCards(@PathVariable String clientId,
                                               @RequestBody List<CreditCard> newCreditCards) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Client client = null;
        CreditCard newCreditCard = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client data");

            client = Administrator.searchByID(clients, clientId);
            for (CreditCard creditCard : newCreditCards) {
                newCreditCard = new CreditCard(creditCard);

                if(CreditCard.isNewCardValid(clients, newCreditCard)) {
                    client.getCreditCards().add(newCreditCard);
                    logger.info("New credit card "+ newCreditCard + " was successfully added");
                }
            }
        }
        return client != null ? client.getCreditCards() : null;
    }

    // http://localhost:8080/client/v2/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete Credit Card",
            description = "Delete an existing credit card by card number")
    @DeleteMapping("/{clientId}/{cardNumber}")
    @ResponseBody
    public List<CreditCard> deleteCard(@PathVariable String clientId,
                                       @PathVariable String cardNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreditCard creditCardToDelete = null;
        Client client = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client data");

            client = Administrator.searchByID(clients, clientId);
            creditCardToDelete = client.searchByCardNumber(cardNumber);

            if (creditCardToDelete == null) {
                logger.warn("Card with number " + cardNumber + " not found");
                return null;
            }

            if (CreditCard.isCardAvailableToDelete(creditCardToDelete)) {
                client.getCreditCards().remove(creditCardToDelete);
            }
            return client.getCreditCards();
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }
}