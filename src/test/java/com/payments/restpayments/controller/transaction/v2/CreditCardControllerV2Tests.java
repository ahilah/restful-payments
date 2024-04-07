package com.payments.restpayments.controller.transaction.v2;

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
public class CreditCardControllerV2Tests {
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
    public void testGetCardTypesCount_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/card")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testGetCardTypeCount_Unauthorized() throws Exception {
        String cardType = "visa";
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/card/" + cardType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUpdateCreditCard_Unauthorized() throws Exception {
        String cardNumber = "968";
        mockMvc.perform(MockMvcRequestBuilders.get("/card/v2/update/" + cardNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testBulkAddCreditCards_Unauthorized() throws Exception {
        String clientID = "127";
        mockMvc.perform(MockMvcRequestBuilders.get("/card/v2/bulkAdd/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testDeleteCard_Unauthorized() throws Exception {
        String clientID = "340";
        String cardNumber = "968";
        mockMvc.perform(MockMvcRequestBuilders.get("/card/v2/bulkAdd/" + clientID + "/" + cardNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}