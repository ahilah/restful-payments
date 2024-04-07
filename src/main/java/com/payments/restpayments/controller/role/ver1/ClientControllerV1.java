package com.payments.restpayments.controller.role.ver1;

import com.payments.restpayments.exception.AccountNotFoundException;
import com.payments.restpayments.exception.IncorrectClientParametersException;
import com.payments.restpayments.exception.InsufficientFundsException;
import com.payments.restpayments.exception.UserNotFoundException;
import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.security.UserAuthorizationService;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/client/v1", "/v1/client"})
@Tag(name = "Client API v1", description = "Endpoints for Client API version 1")
public class ClientControllerV1 {
    Authentication authentication;
    UserDetails userDetails;
    private static final Logger logger = LogManager.getLogger(ClientControllerV1.class);
    public ClientControllerV1() {
    }

    // http://localhost:8080/client/v1/me
    @Operation(summary = "Get Full Client Information")
    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?>  getMeClientInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Client client = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            client = Administrator.searchByUsername(clients, userDetails.getUsername());
            if(client != null) {
                logger.info("Endpoint /client/v1/me was called by " + userDetails + LocalDateTime.now());
                return ResponseEntity.ok(client);
            } else {
                logger.warn("Client with username " + userDetails.getUsername() + " was not found.");
                throw new UserNotFoundException("Client with username " + userDetails.getUsername() + " was not found.");
            }
        } else {
            logger.warn("Client was not found or does not exist\t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v1/card/
    @Operation(summary = "Get client credit card by number")
    @GetMapping("/card/{creditCardNumber}")
    @ResponseBody
    public ResponseEntity<?> getClientCreditCard(@PathVariable String creditCardNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client client = Administrator.searchByUsername(clients, userDetails.getUsername());
            logger.info("Endpoint /client/v1/card/ was called by \t" + userDetails + "\t" + LocalDateTime.now());

            CreditCard creditCard = client.searchByCardNumber(creditCardNumber);
            if(creditCard != null) {
                return ResponseEntity.ok(creditCard);
            } else {
                logger.warn("Credit Card with number " + creditCardNumber + " was not found or does not exist");
                throw new AccountNotFoundException("Credit Card with number "
                        + creditCardNumber + " was not found or does not exist");
            }
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }


    // http://localhost:8080/client/v1/update
    @Operation(summary = "Full update client without any new data checking")
    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateClient(@RequestBody Client client) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client clientOptional = Administrator.searchByID(clients, userDetails.getUsername());
            clients.set(clients.indexOf(clientOptional), client);
            return ResponseEntity.ok(clients.get(clients.indexOf(client)));
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v1/update/part
    @Operation(summary = "Partially update client")
    @PatchMapping("/update/part")
    @ResponseBody
    public ResponseEntity<?> partiallyUpdateClient(@RequestBody Client partialClient) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client existingClient = Administrator.searchByID(clients, userDetails.getUsername());
            if (existingClient != null) {
                existingClient.partiallyUpdate(partialClient);
                return ResponseEntity.ok(existingClient);
            } else {
                logger.warn("Client with ID " + userDetails.getUsername() + " not found.");
                throw new UserNotFoundException("Client with username " + userDetails.getUsername() + " not found.");
            }
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v1/update/detail
    @Operation(summary = "Update client details")
    @PatchMapping("/update/detail")
    @ResponseBody
    public ResponseEntity<?> clientDetailsUpdate(@RequestBody Client clientDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            if(clientDetails.getPassword() != null || clientDetails.getClientID() != null) {
                return ResponseEntity.ok("Client details were updated");
            } else {
                logger.warn("Client with ID " + userDetails.getUsername() + " not found.");
                throw new UserNotFoundException("Client with username " + userDetails.getUsername() + " not found.");
            }
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }


    // http://localhost:8080/client/v1/block/
    @Operation(summary = "Update client blocked status")
    @PatchMapping("/block/{accountId}")
    @ResponseBody
    public ResponseEntity<?> updateClientBlockedStatus(@PathVariable int accountId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client client = Administrator.searchByID(clients, userDetails.getUsername());
            if (client != null) {
                List<CreditCard> creditCards = client.getCreditCards();
                if (!creditCards.isEmpty()) {
                    for (CreditCard creditCard : creditCards) {
                        if (creditCard.getAccount().getId() == accountId) {
                            client.blockAccount(creditCard.getAccount());
                            logger.info("Account with ID " + accountId + " was successfully blocked.");
                            return ResponseEntity.ok(creditCard.getAccount().getId());
                        }
                    }
                } else {
                    logger.warn("No credit cards associated with this client.");
                }
            } else {
                logger.warn("Client with username " + userDetails.getUsername() + " not found.");
                throw new IncorrectClientParametersException("Client with username " +
                        userDetails.getUsername() + " not found.");
            }
            return ResponseEntity.ok(client);
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/client/v1/del/
    @Operation(summary = "Delete client credit cards")
    @DeleteMapping("/del")
    @ResponseBody
    public ResponseEntity<?> deleteCards() throws InsufficientFundsException {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_USER")) {
            Client client = Administrator.searchByID(clients, userDetails.getUsername());
            List<CreditCard> creditCards = client.getCreditCards();
            for (CreditCard creditCard : creditCards) {
                Account account = creditCard.getAccount();
                if (account.getBalance() != 0) {
                    logger.warn("Can not close cards of user " +
                            userDetails.getUsername() + " because of non-zero balance");
                    throw new InsufficientFundsException("Can not close cards of user "
                            + userDetails.getUsername() + " because of non-zero balance");
                } else {
                    client.blockAccount(account);
                    logger.info("Account with ID " + " successfully blocked");
                }
            }
            creditCards.clear();
            return ResponseEntity.ok("Credit Card were successfully closed and deleted");
        } else {
            logger.warn("Client was not found or does not exist \t" + userDetails + "\t" + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }
}