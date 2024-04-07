package com.payments.restpayments.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;

public class UserAuthorizationService {
    public static boolean hasUserRole(@NotNull Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }
}

/* public class RoleAuth {
    public static boolean isAdmin(@NotNull Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
*/