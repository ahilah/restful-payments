package com.payments.restpayments.controller.role.ver1;

import com.payments.restpayments.exception.AccountNotFoundException;
import com.payments.restpayments.exception.AdministratorNotFoundException;
import com.payments.restpayments.exception.IncorrectClientParametersException;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.payments.restpayments.RestPaymentsApplication.admins;
import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/api/v1/admin", "/api/admin/v1"})
@Tag(name = "API v1 Administrator", description = "Endpoints for Administrator API version 1")
public class AdministratorControllerV1 {
    private static final Logger logger = LogManager.getLogger(AdministratorControllerV1.class);
    Authentication authentication;
    UserDetails userDetails;

    // http://localhost:8080/api/v1/admin/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of clients",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Operation(summary = "Get all clients")
    @GetMapping("/")
    @ResponseBody
    public List<Client> getAlClients() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client list");
            return clients;
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/client/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Retrieve client by ID", description = "")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved client by ID",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Client.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "clientID", description = "ID of the client to be retrieved", required = true)
    @GetMapping("/{clientID}")
    @ResponseBody
    public Client getClientByID(@PathVariable String clientID) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client with ID " + clientID);
            return Administrator.searchByID(clients, clientID);
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/client/name
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get client by first name")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved client by first name",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Client not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "firstName", description = "First name of the client to be retrieved", required = true)
    @GetMapping("/client/name/{firstName}")
    @ResponseBody
    public ResponseEntity<Client> getClientByName(@PathVariable String firstName) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client by first name");

            for (Client client : clients) {
                if (client.getFirstName().equalsIgnoreCase(firstName)) {
                    return ResponseEntity.ok(client);
                }
            }
            return ResponseEntity.notFound().build();
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/client/name/full
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get client by both first and last name")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved client by both first and last name",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Client not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "firstName", description = "First name of the client to be retrieved", required = true)
    @Parameter(name = "lastName", description = "Last name of the client to be retrieved", required = true)
    @GetMapping("/client/name/full")
    @ResponseBody
    public ResponseEntity<Client> getClientByNameAndLastName(@RequestParam("firstName") String firstName,
                                                             @RequestParam("lastName") String lastName) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client by first and last name");

            Pattern firstNamePattern = Pattern.compile(firstName, Pattern.CASE_INSENSITIVE);
            Pattern lastNamePattern = Pattern.compile(lastName, Pattern.CASE_INSENSITIVE);

            for (Client client : clients) {
                Matcher firstNameMatcher = firstNamePattern.matcher(client.getFirstName());
                Matcher lastNameMatcher = lastNamePattern.matcher(client.getLastName());
                if (firstNameMatcher.find() && lastNameMatcher.find()) {
                    return ResponseEntity.ok(client);
                }
            }
            return ResponseEntity.notFound().build(); // 404
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/client?
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get client by name regex")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved client by name regex",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Client not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "name", description = "Name or part of the name of the client to be retrieved", required = true)
    @GetMapping("/client")
    @ResponseBody
    public ResponseEntity<Client> getClientByNameRegex(@RequestParam("name") String name) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client by name regex");

            Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
            for (Client client : clients) {
                Matcher matcher = pattern.matcher(client.getFirstName());
                if (matcher.find()) {
                    return ResponseEntity.ok(client);
                }
            }
            return ResponseEntity.notFound().build();
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/client/add
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new client")
    @ApiResponse(responseCode = "200", description = "Client created successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "client", description = "New client to add", required = true)
    @PostMapping("/client/add")
    @ResponseBody
    public Client createClient(@RequestBody Client client) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            return addNewClient(client);
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    private @Nullable Client addNewClient(Client client) {
        Client newClient = new Client(client);
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            clients.add(newClient);
            logger.info("New client " + newClient + " was successfully added");
            return clients.get(clients.indexOf(newClient));
        } else {
            logger.warn("Client " + newClient + " was not added");
            return null;
        }
    }

    // http://localhost:8080/api/v1/admin/update/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Full update a client by ID")
    @ApiResponse(responseCode = "200", description = "Client updated successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Client not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "clientId", description = "ID of the client to be updated", required = true)
    @PutMapping("/update/{clientId}")
    @ResponseBody
    public Client updateClient(@PathVariable String clientId, @RequestBody Client client) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested a client update");

            Client newDataClient = new Client(client);
            Client oldDataClient = Administrator.searchByID(clients, clientId);

            validateClientData(newDataClient, oldDataClient);

            clients.set(clients.indexOf(oldDataClient), newDataClient);
            logger.info("Client: " + oldDataClient + " was successfully updated: " + newDataClient);
            return newDataClient;
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    private void validateClientData(@NotNull Client newDataClient, @NotNull Client oldDataClient) {
        if (newDataClient.getClientID().equalsIgnoreCase(oldDataClient.getClientID()) &&
                !newDataClient.getCreditCards().isEmpty()) {
            for (Client rclient : clients) {
                for (CreditCard creditCard : newDataClient.getCreditCards()) {
                    if (rclient.searchByCardNumber(creditCard.getCardNumber()) != null) {
                        logger.warn("Client has incorrect credit card parameters: " + newDataClient);
                        throw new IncorrectClientParametersException("You are not authorized to access this resource");
                    }
                }
            }
        } else {
            logger.warn("Client has incorrect parameters: " + newDataClient);
            throw new IncorrectClientParametersException("You are not authorized to access this resource");
        }
    }


    // http://localhost:8080/api/v1/admin/client/bulkAdd
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Group clients adding")
    @ApiResponse(responseCode = "200", description = "Clients added successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @Parameter(name = "newClients", description = "List of new clients to add", required = true)
    @PostMapping("/client/bulkAdd")
    @ResponseBody
    public List<Client> bulkAddClients(@RequestBody List<Client> newClients) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        List<Client> newDataClients = new ArrayList<>(10);
        Client newClient = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested a client bulk adding");
            for (Client client : newClients) {
                newClient = addNewClient(client);
                if(newClient != null) newDataClients.add(newClient);
            }
            return newDataClients;
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/unblock/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Unblock an client account")
    @ApiResponse(responseCode = "200", description = "Account unblocked successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @Parameter(name = "accountId", description = "ID of the account to be unblocked", required = true)
    @PatchMapping("/unblock/{accountId}")
    @ResponseBody
    public Account unblockAccount(@PathVariable int accountId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();

        Administrator admin = null;
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested client unblock account " + accountId);

            for(Administrator administrator : admins) {
                if(administrator.getUsername().equals(userDetails.getUsername())) admin = administrator;
            }

            if (admin != null) {
                for (Client client : clients) {
                    for (CreditCard creditCard : client.getCreditCards()) {
                        Account account = creditCard.getAccount();
                        if (account.getId() == accountId) {
                            logger.info("Account with ID: " + accountId + " was successfully found");
                            admin.removeAccountBlock(creditCard.getAccount());
                            return account;
                        }
                    }
                } throw new AccountNotFoundException("Account with ID: " + accountId + " was not found");
            } else throw new AdministratorNotFoundException("Admin with id "
                    + userDetails.getUsername() + " not found");
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

    // http://localhost:8080/api/v1/admin/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a client by ID")
    @ApiResponse(responseCode = "200", description = "Client deleted successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized access attempt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessDeniedException.class)))
    @ApiResponse(responseCode = "404", description = "Client not found")
    @Parameter(name = "clientId", description = "ID of the client to be deleted", required = true)
    @DeleteMapping("/{clientId}")
    @ResponseBody
    public List<Client> deleteClient(@PathVariable String clientId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();

        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Admin " + userDetails.getUsername() + " has requested deleting client");
            // Check if the client with the given ID exists
            Optional<Client> clientToDelete = clients.stream()
                    .filter(client -> Objects.equals(client.getClientID(), clientId))
                    .findFirst();

            if (clientToDelete.isPresent()) {
                // Check if all accounts have zero balance and are blocked
                boolean allAccountsZeroBalanceAndBlocked = clientToDelete.get().getCreditCards().stream()
                        .allMatch(card -> card.getAccount().getBalance() == 0 && card.getAccount().isBlocked());

                if (allAccountsZeroBalanceAndBlocked) {
                    clients.removeIf(client -> Objects.equals(client.getClientID(), clientId));
                    return clients;
                } else {
                    logger.warn("Cannot delete client " + clientId + " as not all accounts have zero balance or are not blocked");
                    throw new IllegalStateException("Cannot delete client as not all accounts have zero balance or are not blocked");
                }
            } else {
                logger.warn("Client with ID " + clientId + " not found");
                throw new IllegalArgumentException("Client with ID " + clientId + " not found");
            }
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }
}

/*
// http://localhost:8080/admin/v1/update/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Full update a client by ID")
    @PutMapping("/update/{clientId}")
    @ResponseBody
    public Client updateClient(@PathVariable String clientId, @RequestBody Client client) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            logger.info("Admin " + userDetails.getUsername() + " has requested a client update");

            Client newDataClient = new Client(client);
            Client oldDataClient = Administrator.searchByID(clients, clientId);

            if (newDataClient.getClientID().equalsIgnoreCase(oldDataClient.getClientID()) &&
                    !newDataClient.getCreditCards().isEmpty()) {

                for(Client rclient : clients) {
                    for(CreditCard creditCard : newDataClient.getCreditCards()) {
                        if(rclient.searchByCardNumber(creditCard.getCardNumber()) != null) {
                            logger.warn("Client has incorrect credit card parameters: " + newDataClient);
                            throw new IncorrectClientParametersException("You are not authorized to access this resource");
                        }
                    }
                }
                clients.set(clients.indexOf(oldDataClient), newDataClient);
                logger.info("Client: " + oldDataClient +
                        " was successfully updated: " + newDataClient);
                return clients.get(clients.indexOf(newDataClient));
            } else {
                logger.warn("Client has incorrect parameters: " + newDataClient);
                throw new IncorrectClientParametersException("You are not authorized to access this resource");
            }
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }
  }

public Client createClient(@RequestBody Client client) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (isAdmin(authentication)) {
            logger.info("Admin " + userDetails.getUsername() + " has created and added new client");
            Client newClient = new Client(client);
            if(newClient.isValidNewClient(clients)) {
                clients.add(newClient);
                logger.info("New client " + newClient + " was successfully added");
                return clients.get(clients.indexOf(newClient));
            } else {
                logger.warn("Client " + newClient + " was not added");
                return null;
            }
        } else {
            logger.warn("Unauthorized access attempt by: " + userDetails);
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }

**/