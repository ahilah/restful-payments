package com.payments.restpayments.controller.role.ver2;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.CreditCard;
import com.payments.restpayments.transaction.Payment;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/client/v2", "/v2/client"})
@Tag(name = "Client API v2", description = "Endpoints for Client API version 2")
public class ClientControllerV2 {
    private static final Logger logger = LogManager.getLogger(ClientControllerV2.class);

    public ClientControllerV2() {
    }

    // http://localhost:8080/client/v2/card
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all credit cards for client")
    @GetMapping("/card")
    @ResponseBody
    public List<CreditCard> getAllCreditCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v2/card was called by \t" + userDetails + "\t" + LocalDateTime.now());

            return client.getCreditCards();
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v2/card/available
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get available credit cards for client")
    @GetMapping("/card/available")
    @ResponseBody
    public Set<CreditCard> getAvailableCreditCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v2/card/available was called by \t" +
                    userDetails + "\t" + LocalDateTime.now());

            Set<CreditCard> creditCards = new HashSet<>();
            for (CreditCard creditCard : client.getCreditCards()) {
                if (!creditCard.getAccount().isBlocked()) {
                    creditCards.add(creditCard);
                }
            }
            return creditCards;
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v2/blocked
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get blocked credit cards by a client")
    @GetMapping("/card/blocked")
    @ResponseBody
    public Set<CreditCard> getBlockedCreditCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v2/card/blocked was called by \t" +
                    userDetails + "\t" + LocalDateTime.now());

            Set<CreditCard> creditCards = new HashSet<>();
            for (CreditCard creditCard : client.getCreditCards()) {
                if (creditCard.getAccount().isBlocked()) {
                    creditCards.add(creditCard);
                }
            }
            return creditCards;
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v2/update
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Full update client information")
    @PutMapping("/update")
    @ResponseBody
    public Client updateClient(@RequestBody Client client) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client clientOptional = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v2/update was called by \t" +
                    userDetails + "\t" + LocalDateTime.now());

            clients.set(clients.indexOf(clientOptional), client);
            return clients.get(clients.indexOf(client));
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v2/payment/
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Make payment transaction between two credit cards")
    @PatchMapping("/payment/{senderCreditCardNumber}/{receiverCreditCardNumber}/{amount}")
    /*@GetMapping("/payment/{senderCreditCardNumber}/{receiverCreditCardNumber}/{amount}")*/
    @ResponseBody
    public Payment makePaymentTransaction(@PathVariable String senderCreditCardNumber,
                                          @PathVariable String receiverCreditCardNumber,
                                          @PathVariable double amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreditCard senderCreditCard = null,
                receiverCreditCard = null;
        Client clnt = null;
        Payment payment = null;

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            clnt = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v2/payment/ was called by \t" +
                    userDetails + "\t" + LocalDateTime.now());

            senderCreditCard = clnt.searchByCardNumber(senderCreditCardNumber);

            for (Client client : clients) {
                receiverCreditCard = client.searchByCardNumber(receiverCreditCardNumber);
                if(receiverCreditCard != null) break;
            }

            if ((senderCreditCard != null) && (receiverCreditCard != null)) {
                payment = senderCreditCard.processPayment(receiverCreditCard, amount);
                if(payment != null) {
                    logger.info("Payment was successful. Sender card: " + senderCreditCard +
                            "\treceiver card: " + receiverCreditCard + "\tamount: " + amount);
                    senderCreditCard.getPayments().add(payment);
                    receiverCreditCard.getPayments().add(payment);
                } else {
                    logger.warn("Payment was not successful. Sender card: " + senderCreditCard +
                            "\treceiver card: " + receiverCreditCard + "\tamount: " + amount);
                }
            } else {
                System.out.println("Client with Credit Card ID " + senderCreditCardNumber + " not found.");
            }
        }
        return payment;
    }

    // http://localhost:8080/client/v2/card/
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Delete a credit card for a client by card number")
    @DeleteMapping("/card/{cardNumber}")
    @ResponseBody
    public List<CreditCard> deleteCard(@PathVariable String cardNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreditCard creditCardToDelete = null;
        Client clnt = null;

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            clnt = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v2/card/ was called by \t" +
                    userDetails + "\t" + LocalDateTime.now());

            creditCardToDelete = clnt.searchByCardNumber(cardNumber);
            if (creditCardToDelete == null) {
                logger.warn("Card with number " + cardNumber + " not found");
                return null;
            }

            if (CreditCard.isCardAvailableToDelete(creditCardToDelete)) {
                clnt.getCreditCards().remove(creditCardToDelete);
            }
            return clnt.getCreditCards();
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }
}