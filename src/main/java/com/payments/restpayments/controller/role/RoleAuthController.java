package com.payments.restpayments.controller.role;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping({"/api/role"})
public class RoleAuthController {
    private static final Logger logger = LogManager.getLogger(RoleAuthController.class);

    // http://localhost:8080/api/role
    @GetMapping("")
    /*@PreAuthorize("isAuthenticated()")*/
    public List<String> getUserRoles() {
        List<String> roles = rolesUser();
        logger.info("Endpoint /roles was called");
        return roles;
    }

    private List<String> rolesUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

}