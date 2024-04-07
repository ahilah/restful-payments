package com.payments.restpayments.controller.role.v1;

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
public class ClientControllerV1Tests {
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
    public void testGetMeClientInfo_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testGetClientCreditCard_Unauthorized() throws Exception {
        String cardID = "666";
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/card/" + cardID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUpdateClient_Unauthorized() throws Exception {
        String clientID = "666";
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/update/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testPartiallyUpdateClient_Unauthorized() throws Exception {
        String clientID = "666";
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/update/part/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testClientDetailsUpdate_Unauthorized() throws Exception {
        String clientID = "666";
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/update/detail/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUpdateClientBlockedStatus_Unauthorized() throws Exception {
        int accountID = 381;
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/block/" + accountID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testDeleteCards_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/client/v1/del")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}