package com.payments.restpayments;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.payments.restpayments.RestPaymentsApplication.admins;
import static com.payments.restpayments.RestPaymentsApplication.clients;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    static List<UserDetails> userDetailsList = new ArrayList<>();

    // http://localhost:8080/login
    // http://localhost:8080/logout

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/swagger-ui/index.html", "/components/**").permitAll()
                .requestMatchers("/v3/api-docs").permitAll()
                .requestMatchers("/swagger-ui/", "/resources/**", "/static/**",
                        "/css/**", "/js/**", "/images/**", "/webjars/**", "/error/**").permitAll()
                .requestMatchers( "/api/v1/payment/**", "/api/v2/card/**").hasRole("ADMIN")
                .requestMatchers("/api/client/**", "/api/account/**", "/api/v2/payment/**",
                        "/api/v1/card/update/", "/api/v1/card/add").hasRole("USER")
                .requestMatchers("/role/**", "/api/v1/card/",
                        "api/super/**","/api/admin/**").permitAll()
                .and()
                .httpBasic()
                //.anyRequest().authenticated()
                .and()
                .formLogin()
                /*.loginPage()*/
                .defaultSuccessUrl("/home.html", true);

        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/v3/**", "/swagger-ui/**",
                                "/log/**", "/error/**", "/home/**").permitAll()
                        .anyRequest().authenticated()
                );

        return  httpSecurity.build();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        // Adding existing admin and regular user
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("1234")
                .roles("ADMIN")
                .build();
        userDetailsList.add(admin);

        UserDetails regularUser = User.withDefaultPasswordEncoder()
                .username("user")
                .password("4321")
                .roles("USER")
                .build();
        userDetailsList.add(regularUser);

        List<UserDetails> adminUsers = admins.stream()
                .map(administrator -> User.withDefaultPasswordEncoder()
                        .username(administrator.getUsername())
                        .password(administrator.getPassword())
                        .roles("ADMIN")
                        .build()).toList();
        userDetailsList.addAll(adminUsers);

        List<UserDetails> clientUsers = clients.stream()
                .map(client -> User.withDefaultPasswordEncoder()
                        .username(client.getUsername())
                        .password(client.getPassword())
                        .roles("USER")
                        .build()).toList();
        userDetailsList.addAll(clientUsers);

        return new InMemoryUserDetailsManager(userDetailsList);
    }

    public static void updateClientAuthDetails(UserDetails userDetails, Client partialClient) {
        Optional<UserDetails> optionalUser = userDetailsList.stream()
                .filter(user -> user.getUsername().equals(userDetails.getUsername()))
                .findFirst();

        if (optionalUser.isPresent()) {
            UserDetails existingUser = optionalUser.get();

            // Оновлюємо ім'я користувача, якщо він був змінений
            if (partialClient.getUsername() != null) {
                existingUser = User.withDefaultPasswordEncoder()
                        .username(partialClient.getUsername())
                        .password(existingUser.getPassword())
                        .roles(existingUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toArray(String[]::new))
                        .build();
            }

            // Оновлюємо пароль користувача, якщо він був змінений
            if (partialClient.getPassword() != null) {
                existingUser = User.withDefaultPasswordEncoder()
                        .username(existingUser.getUsername())
                        .password(partialClient.getPassword())
                        .roles(existingUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toArray(String[]::new))
                        .build();
            }

            // Оновлення користувача в списку userDetailsList
            UserDetails finalExistingUser = existingUser;
            userDetailsList.removeIf(user -> user.getUsername().equals(finalExistingUser.getUsername()));
            userDetailsList.add(existingUser);
        }
    }

    public static void updateAdminAuthDetails(UserDetails userDetails, Administrator partialAdmin) {
        Optional<UserDetails> optionalUser = userDetailsList.stream()
                .filter(user -> user.getUsername().equals(userDetails.getUsername()))
                .findFirst();

        if (optionalUser.isPresent()) {
            UserDetails existingUser = optionalUser.get();

            // Оновлюємо ім'я користувача, якщо він був змінений
            if (partialAdmin.getUsername() != null) {
                existingUser = User.withDefaultPasswordEncoder()
                        .username(partialAdmin.getUsername())
                        .password(existingUser.getPassword())
                        .roles(existingUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toArray(String[]::new))
                        .build();
            }

            // Оновлюємо пароль користувача, якщо він був змінений
            if (partialAdmin.getPassword() != null) {
                existingUser = User.withDefaultPasswordEncoder()
                        .username(existingUser.getUsername())
                        .password(partialAdmin.getPassword())
                        .roles(existingUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toArray(String[]::new))
                        .build();
            }

            // Оновлення користувача в списку userDetailsList
            UserDetails finalExistingUser = existingUser;
            userDetailsList.removeIf(user -> user.getUsername().equals(finalExistingUser.getUsername()));
            userDetailsList.add(existingUser);
        }
    }

    public static <T> boolean isNewUserCreated(T userT, String role) {
        UserDetails user = null;
        Client client = null;
        Administrator admin = null;
        boolean isUserAdded = false;
        if(userT instanceof Client) {
            client = (Client) userT;
            user = User.withDefaultPasswordEncoder()
                    .username(client.getUsername())
                    .password(client.getPassword())
                    .roles(role)
                    .build();
            isUserAdded = true;
            userDetailsList.add(user);
        }
        else if(userT instanceof Administrator) {
            admin = (Administrator) userT;
            user = User.withDefaultPasswordEncoder()
                    .username(admin.getUsername())
                    .password(admin.getPassword())
                    .roles(role)
                    .build();
            isUserAdded = true;
            userDetailsList.add(user);
        }
        return isUserAdded;
    }
}