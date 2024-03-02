package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.payments.restpayments.RestPaymentsApplication.admins;
import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/admin/v1", "/v1/admin"})
public class AdministratorControllerV1 {
    List<Client> clientsLocal = clients;
    List<Administrator> adminsLocal = admins;

    // http://localhost:8080/admin/v1/all
    @GetMapping("/all")
    @ResponseBody
    public List<Client> getAlClients() {
        return clientsLocal;
    }

    // http://localhost:8080/admin/v1/client/add
    @PostMapping("/client/add")
    @ResponseBody
    public String createClient(@RequestBody Client client) {
        Client newClient = new Client(client);
        clientsLocal.add(newClient);
        return "Client is successfully added.";
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