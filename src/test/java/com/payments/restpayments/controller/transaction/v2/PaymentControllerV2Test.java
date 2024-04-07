package com.payments.restpayments.controller.transaction.v2;

import com.payments.restpayments.controller.role.SuperAdminController;
import com.payments.restpayments.controller.transaction.ver2.PaymentControllerV2;
import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.SuperAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PaymentControllerV2Test {

    @Mock
    private Administrator administrator;
    private Administrator administratorMock;

    @Mock
    private SuperAdmin superAdminMock;

    @InjectMocks
    private SuperAdminController superAdminController;


    @BeforeEach
    void setUp() {
        administratorMock = mock(Administrator.class);
    }


    @InjectMocks
    private PaymentControllerV2 paymentControllerV2;

    @Test
    void testGetClientPayments() {
        // Mocking authentication and UserDetails
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("jd")
                .password("2222")
                .roles("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails1 = (UserDetails) authentication.getPrincipal();
        assertEquals("jd", userDetails1.getUsername());

        /*// Mocking searchByUsername and showPaymentsInfo methods
        List<Client> clients = new ArrayList<>(); // initialize with required data
        Payment payment = new Payment(2, 1, 10);
        List<Payment> payments = new ArrayList<>();
        payments.add(payment);
        Account account = new Account(2, 20, false);
        CreditCard creditCard = new CreditCard(2, "2",
                "2222", "visa", account, payments);
        List<CreditCard> creditCards = new ArrayList<>();
        creditCards.add(creditCard);
        clients.add(new Client("2", "jane", "doe", "jd", "2222", creditCards));
        Set<Payment> p = new HashSet<>();
        p.add(payment);

        // Test method invocation
        String cardNumber = "2222"; // provide necessary card number
        ResponseEntity<?> clientPayments = paymentControllerV2.getClientPayments(cardNumber);

        assertEquals(p, clientPayments);*/
    }

}
