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
public class AdministratorControllerV1Tests {
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
    public void testGetAllClients_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/admin/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testGetClientByID_Unauthorized() throws Exception {
        String clientID = "666";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetClientByName_Unauthorized() throws Exception {
        String clientName = "user";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/name" + clientName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetClientByNameAndLastName_Unauthorized() throws Exception {
        String clientName = "user";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/name/full/" + clientName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetClientByNameRegex_Unauthorized() throws Exception {
        String clientName = "user";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/name?" + clientName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateClient_Unauthorized() throws Exception {
        String clientID = "232";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/add/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdateClient_Unauthorized() throws Exception {
        String clientID = "414";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/update/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testBulkAddClients_Unauthorized() throws Exception {
        int accountID = 253;
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/unblock/" + accountID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteClient_Unauthorized() throws Exception {
        String clientID = "414";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v1/client/" + clientID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}