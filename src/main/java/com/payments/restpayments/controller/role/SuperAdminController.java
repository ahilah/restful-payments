package com.payments.restpayments.controller.role;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.SuperAdmin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.payments.restpayments.RestPaymentsApplication.admins;

@RestController
@RequestMapping("/api/super")
public class SuperAdminController {
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Logger logger = LogManager.getLogger(SuperAdminController.class);

    private String jwTkn;

    // Authorization: Bearer

    // http://localhost:8080/api/super/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody SuperAdmin credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Отримати адміністратора за ім'ям користувача
        SuperAdmin superAdmin = SuperAdmin.getInstance();

        // Перевірити, чи існує адміністратор з таким ім'ям користувача та чи вірний пароль
        if (superAdmin != null
                && superAdmin.getPassword().equals(password)
                && superAdmin.getUsername().equals(username)) {
            // Генерувати JWT токен та повернути його
            String jwtToken = generateJWTToken(superAdmin.getId());
            return ResponseEntity.ok(jwtToken);
        } else {
            // Невірні дані для входу
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String generateJWTToken(int adminId) {
        // Тип підпису та підпис для JWT токена
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // Час створення та закінчення токена
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long tokenExpirationMillis = 3600000; // 1 година
        Date expirationDate = new Date(nowMillis + tokenExpirationMillis);

        // Створення JWT токена
        String jwtToken = Jwts.builder()
                .setId(Integer.toString(adminId))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(secretKey, signatureAlgorithm)
                .compact();

        return jwtToken;
    }

    // http://localhost:8080/api/super/all
    @Operation(summary = "Get all administrators")
    @GetMapping("/all")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of administrators",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SuperAdmin.class)))
    @ResponseBody
    public ResponseEntity<List<Administrator>> getAllAdmins(@RequestHeader(required = false, value = "Authorization")
                                                                String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            SuperAdmin superAdmin = validateJWTToken(jwtToken);
            SuperAdmin admin = SuperAdmin.getInstance();
            if (superAdmin != null) {
                if (superAdmin.getUsername().equals(admin.getUsername())
                        && superAdmin.getPassword().equals(admin.getPassword())) {
                    return ResponseEntity.ok(admins);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Метод для перевірки та розшифрування JWT токена
    private SuperAdmin validateJWTToken(String jwtToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            int adminId = Integer.parseInt(claims.getId());
            SuperAdmin administrator = SuperAdmin.getInstance();
            if (adminId == administrator.getId()) {
                return administrator;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // http://localhost:8080/api/super/add/admin
    @Operation(summary = "Create a new administrator")
    @PostMapping("/add/admin")
    @ResponseBody
    public ResponseEntity<Object> createAdmin(@RequestBody Administrator newAdmin,
                                              @RequestHeader(required = false, value = "Authorization")
                                              String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            SuperAdmin superAdmin = validateJWTToken(jwtToken);
            logger.info("Endpoint /admin/add was called by super admin" + validateJWTToken(jwtToken)
                    + "\t on " + LocalDateTime.now());
            SuperAdmin admin = SuperAdmin.getInstance();
            if (superAdmin != null) {
                if (superAdmin.getUsername().equals(admin.getUsername())
                        && superAdmin.getPassword().equals(admin.getPassword())) {
                    if (newAdmin.isValidNewAdmin(admins)) {
                            admins.add(newAdmin);
                            logger.info("Admin " + newAdmin.getUsername() + " was successfully added to ADMINS");
                            logger.info("Admin with username " + newAdmin.getUsername() + " was successfully added");
                            return ResponseEntity.ok(newAdmin);
                    } else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    // Якщо користувач не є суперадміном, повертаємо відмову у доступі
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
                }
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}