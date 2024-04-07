package com.payments.restpayments.controller.role.ver2;

import com.payments.restpayments.exception.AdministratorNotFoundException;
import com.payments.restpayments.exception.SimilarUserException;
import com.payments.restpayments.exception.UserNotFoundException;
import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.security.UserAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.payments.restpayments.RestPaymentsApplication.admins;

@RestController
@RequestMapping({"/admin/v2", "/v2/admin"})
@Tag(name = "Administrator API v2", description = "Endpoints for Administrator API version 2")
public class AdministratorControllerV2 {
    private static final Logger logger = LogManager.getLogger(AdministratorControllerV2.class);
    Authentication authentication;
    UserDetails userDetails;

    /*// http://localhost:8080/admin/v2/all
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all administrators")
    @GetMapping("/all")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of administrators",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Administrator.class)))
    @ResponseBody
    public List<Administrator> getAlAdmins() {
        return admins;
    }*/

    // http://localhost:8080/admin/v2/me
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Full Admin Information")
    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?> getMeAdminInfo() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            Administrator admin = Administrator.searchByUsername(admins, userDetails.getUsername());
            if(admin != null) {
                logger.info("Endpoint /admin/all was called by " + userDetails + LocalDateTime.now());
                logger.info("Admin with username " + userDetails.getUsername() + " was successfully found");
                return ResponseEntity.ok(admin);
            } else {
                logger.warn("Admin with username " + userDetails.getUsername() + " was not found in database");
                throw new UserNotFoundException("Admin with username " +
                        userDetails.getUsername() + " not found in database");
            }
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/admin/v2/name/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get administrator by username")
    @GetMapping("/name/{adminUsername}")
    @ResponseBody
    public ResponseEntity<?> getAdminByUsername(@PathVariable String adminUsername) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Endpoint /admin/name/ was called by " + userDetails + LocalDateTime.now());
            for (Administrator admin : admins) {
                if (admin.getUsername().equalsIgnoreCase(adminUsername)) {
                    logger.info("Admin with username " + userDetails.getUsername() + " was successfully found");
                    return ResponseEntity.ok(admin);
                }
            }
            logger.warn("Admin with username " + userDetails.getUsername() + " was not found");
            return ResponseEntity.notFound().build();
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/admin/v2/rname/adminRegex?
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get admin by username regex")
    @GetMapping("/rname/{adminRegex}")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the admin by username regex",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Administrator.class)))
    @Parameter(name = "adminRegex", description = "Regular expression to match the admin's username",
            required = true, example = ".*")
    @ResponseBody
    public ResponseEntity<?> getAdminByNameRegex(@PathVariable String adminUsername) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Endpoint /admin/rname/adminRegex? was called by " + userDetails + LocalDateTime.now());

            Pattern pattern = Pattern.compile(adminUsername, Pattern.CASE_INSENSITIVE);

            for (Administrator admin : admins) {
                Matcher matcher = pattern.matcher(admin.getUsername());
                if (matcher.find()) {
                    logger.info("Admin with username " + userDetails.getUsername() + " was successfully found");
                    return ResponseEntity.ok(admin);
                }
            }
            logger.warn("Admin with username " + userDetails.getUsername() + " was not found");
            return ResponseEntity.notFound().build();
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }


    // http://localhost:8080/admin/v2/add
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new administrator")
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> createAdmin(@RequestBody Administrator admin) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            logger.info("Endpoint /admin/add was called by " + userDetails + LocalDateTime.now());
            Administrator administrator = new Administrator(admin);
            if(administrator.isValidNewAdmin(admins)) {
                admins.add(administrator);
                logger.info("Admin with username " + userDetails.getUsername() + " was successfully added");
                return ResponseEntity.ok(admins.get(admins.lastIndexOf(administrator)));
            } else throw new SimilarUserException("Admin has already exists");
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/admin/v2/update/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Full update administrator")
    @PutMapping("/update/{adminId}")
    @ResponseBody
    public ResponseEntity<?> updateAdmin(@RequestBody Administrator admin) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            Administrator administrator = new Administrator(admin);
            Administrator adminOptional = Administrator.searchByID(admins, userDetails.getUsername());
            if(administrator.isValidNewAdmin(admins) && adminOptional != null) {
                // SecurityConfig.updateAdminAuthDetails(userDetails, administrator);
                admins.set(admins.indexOf(adminOptional), administrator);
                return ResponseEntity.ok(admins.get(admins.indexOf(administrator)));
            } else throw new SimilarUserException("Admin has already exists");
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }

    // http://localhost:8080/v2/admin/update/part
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Partially update administrator")
    @PatchMapping("/update/part")
    @ResponseBody
    public ResponseEntity<?> partiallyUpdateAdmin(@RequestBody Administrator partialAdmin) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            Administrator existingAdmin = Administrator.searchByID(admins, userDetails.getUsername());
            if (existingAdmin != null) {
                // SecurityConfig.updateAdminAuthDetails(userDetails, partialAdmin);
                return ResponseEntity.ok(existingAdmin.partiallyUpdate(partialAdmin));
            }
            throw new AdministratorNotFoundException("Admin with username " + userDetails.getUsername() + " was not found");
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }


    // http://localhost:8080/admin/v2/del/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete administrator")
    @DeleteMapping("/del/{adminId}")
    @ResponseBody
    public ResponseEntity<?> deleteAdmin(@PathVariable String adminId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
        if (UserAuthorizationService.hasUserRole(authentication, "ROLE_ADMIN")) {
            admins.removeIf(admin -> Objects.equals(admin.getAdminID(), adminId));
            return ResponseEntity.ok(admins);
        } else {
            logger.warn("Such user was not found or does not exist" + userDetails + LocalDateTime.now());
            throw new AccessDeniedException("User " +
                    authentication.getDetails() + " are not authorized to access this resource");
        }
    }
}