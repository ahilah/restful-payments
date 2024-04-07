package com.payments.restpayments.controller.role.v2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerV2Tests {
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", "4321",
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );
    }

    @Test
    public void testGetAllCreditCards_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v2/card")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testGetAvailableCreditCards_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v2/card/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testGetBlockedCreditCards_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v2/card/blocked")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUpdateClient_Unauthorized() throws Exception {
        String clientID = "563";
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v2/update/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testMakePaymentTransaction_Unauthorized() throws Exception {
        String senderCard = "563",
                receiverCard = "740";
        double amount = 71.5;
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v2/payment/"
                                + senderCard + "/" + receiverCard + "/" + amount)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testDeleteCard_Unauthorized() throws Exception {
        String cardNumber = "563";
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v2/card/" + cardNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
