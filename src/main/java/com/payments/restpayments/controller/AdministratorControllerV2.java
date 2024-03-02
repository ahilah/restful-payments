package com.payments.restpayments.controller;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.admins;
import static com.payments.restpayments.RestPaymentsApplication.clients;

@RestController
@RequestMapping({"/admin/v2", "/v2/admin"})
public class AdministratorControllerV2 {
    List<Administrator> adminsLocal = admins;

    // http://localhost:8080/admin/v2/all
    @GetMapping("/get/{adminId}")
    @ResponseBody
    public Administrator getAdmin(@PathVariable int adminId) {
        int realId = adminId - 1;
        return adminsLocal.get(realId);
    }


}