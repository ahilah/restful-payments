package com.payments.restpayments.controller.transaction.v2;

import com.payments.restpayments.controller.transaction.ver2.PaymentControllerV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Base64;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentControllerV2.class)
public class PaymentControllerV2Tests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetClientPayments_Success() throws Exception {
        String cardNumber = "961";
        mockMvc.perform(MockMvcRequestBuilders.get("/payment/v2/" + cardNumber)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:4321".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testGetClientPayments_NotFound() throws Exception {
        String cardNumber = "666";
        mockMvc.perform(MockMvcRequestBuilders.get("/payment/v2/" + cardNumber)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /*@Test
    public void hello() {
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
        Assertions.assertEquals("jd", userDetails1.getUsername());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }*/
}

