package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.payments.restpayments.RestPaymentsApplication.admins;
import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/admin/v1", "/v1/admin"})
public class AdministratorControllerV1 {
    List<Client> clientsLocal = clients;
    List<Administrator> adminsLocal = admins;

    // http://localhost:8080/admin/v1/
    @GetMapping("/")
    @ResponseBody
    public List<Client> getAlClients() {
        return clientsLocal;
    }

    // http://localhost:8080/admin/v1/client/name
    @GetMapping("/client/name/{firstName}")
    public ResponseEntity<Client> getClientByName(@PathVariable String firstName) {
        for (Client client : clientsLocal) {
            if (client.getFirstName().equalsIgnoreCase(firstName)) {
                return ResponseEntity.ok(client);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // http://localhost:8080/admin/v1/client/name/full
    @GetMapping("/client/name/full")
    public ResponseEntity<Client> getClientByNameAndLastName(@RequestParam("firstName") String firstName,
                                                             @RequestParam("lastName") String lastName) {
        Pattern firstNamePattern = Pattern.compile(firstName, Pattern.CASE_INSENSITIVE);
        Pattern lastNamePattern = Pattern.compile(lastName, Pattern.CASE_INSENSITIVE);

        for (Client client : clientsLocal) {
            Matcher firstNameMatcher = firstNamePattern.matcher(client.getFirstName());
            Matcher lastNameMatcher = lastNamePattern.matcher(client.getLastName());
            if (firstNameMatcher.find() && lastNameMatcher.find()) {
                return ResponseEntity.ok(client); // Повернути знайденого клієнта
            }
        }
        return ResponseEntity.notFound().build(); // Повернути помилку 404, якщо клієнт не знайдений
    }

    // http://localhost:8080/admin/v1/client
    @GetMapping("/client")
    public ResponseEntity<Client> getClientByNameRegex(@RequestParam("name") String name) {
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

        for (Client client : clientsLocal) {
            Matcher matcher = pattern.matcher(client.getFirstName());
            if (matcher.find()) {
                return ResponseEntity.ok(client);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // http://localhost:8080/admin/v1/client/add
    @PostMapping("/client/add")
    @ResponseBody
    public String createClient(@RequestBody Client client) {
        Client newClient = new Client(client);
        clientsLocal.add(newClient);
        return "Client is successfully added.";
    }

    // http://localhost:8080/admin/v1/update/
    @PutMapping("/update/{clientId}")
    @ResponseBody
    public Client updateClient(@PathVariable int clientId, @RequestBody Client client) {
        Client clnt = new Client(client);
        Client clientOptional = Administrator.searchByID(clientsLocal, clientId);
        clientsLocal.set(clientsLocal.indexOf(clientOptional), clnt);
        return clientsLocal.get(clientsLocal.indexOf(clnt));
    }


    // http://localhost:8080/admin/v1/client/bulkAdd
    @PostMapping("/client/bulkAdd")
    @ResponseBody
    public String bulkAddClients(@RequestBody List<Client> newClients) {
        clientsLocal.addAll(newClients);
        return "Clients successfully added in bulk.";
    }

    // http://localhost:8080/admin/v1/unblock/
    @PatchMapping("/unblock/{adminId}/{accountId}")
    public Account unblockAccount(@PathVariable int adminId, @PathVariable int accountId) {
        Administrator admin = null;
        for(Administrator administrator : adminsLocal) {
            if(administrator.getId() == adminId) admin = administrator;
        }

        for (Client client : clientsLocal) {
            for (CreditCard creditCard : client.getCreditCards()) {
                Account account = creditCard.getAccount();
                if (account.getId() == accountId) {
                    Objects.requireNonNull(admin).removeAccountBlock(creditCard.getAccount());
                    return account;
                }
            }
        }
        return null;
    }

    // http://localhost:8080/admin/v1/
    @DeleteMapping("/{clientId}")
    public List<Client> deleteClient(@PathVariable int clientId) {
        clientsLocal.removeIf(client -> client.getId() == clientId);
        return clientsLocal;
    }
}