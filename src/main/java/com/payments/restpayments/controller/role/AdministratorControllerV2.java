package com.payments.restpayments.controller.role;

import com.payments.restpayments.exception.AdministratorNotFoundException;
import com.payments.restpayments.role.Administrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.payments.restpayments.RestPaymentsApplication.admins;

@RestController
@RequestMapping({"/admin/v2", "/v2/admin"})
public class AdministratorControllerV2 {
    List<Administrator> adminsLocal = admins;

    // http://localhost:8080/admin/v2/all
    @GetMapping("/all")
    @ResponseBody
    public List<Administrator> getAlAdmins() {
        return adminsLocal;
    }

    // http://localhost:8080/admin/v2/
    @GetMapping("/{adminId}")
    @ResponseBody
    public Administrator getAdmin(@PathVariable int adminId) {
        Optional<Administrator> adminOptional = adminsLocal.stream()
                .filter(admin -> admin.getId() == adminId)
                .findFirst();

        return adminOptional.orElse(null);
    }

    // http://localhost:8080/admin/v2/
    @GetMapping("/{adminUsername}")
    public ResponseEntity<Administrator> getAdminByName(@PathVariable String adminUsername) {
        for (Administrator admin : adminsLocal) {
            if (admin.getUsername().equalsIgnoreCase(adminUsername)) {
                return ResponseEntity.ok(admin);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // http://localhost:8080/admin/v2/add
    @PostMapping("/add")
    @ResponseBody
    public Administrator createAdmin(@RequestBody Administrator admin) {
        Administrator administrator = new Administrator(admin);
        adminsLocal.add(administrator);
        return adminsLocal.get(adminsLocal.lastIndexOf(administrator));
    }

    // http://localhost:8080/admin/v2/update/
    @PutMapping("/update/{adminId}")
    @ResponseBody
    public Administrator updateAdmin(@PathVariable int adminId,
                                     @RequestBody Administrator admin) {
        Administrator administrator = new Administrator(admin);
        Administrator adminOptional = Administrator.searchByID(adminsLocal, adminId);
        adminsLocal.set(adminsLocal.indexOf(adminOptional), administrator);
        return adminsLocal.get(adminsLocal.indexOf(administrator));
    }

    // http://localhost:8080/v2/admin/update/part/
    @PatchMapping("/update/part/{adminId}")
    public Administrator partiallyUpdateAdmin(@PathVariable int adminId,
                                              @RequestBody Administrator partialAdmin) {
        Administrator existingAdmin = Administrator.searchByID(adminsLocal, adminId);

        if (existingAdmin == null) {
            throw new AdministratorNotFoundException("Admin with id " + adminId + " not found");
        }
        return existingAdmin.partiallyUpdate(partialAdmin);
    }


    // http://localhost:8080/admin/v2/
    @DeleteMapping("/{adminId}")
    public List<Administrator> deleteAdmin(@PathVariable int adminId) {
        adminsLocal.removeIf(admin -> admin.getId() == adminId);
        return adminsLocal;
    }
}