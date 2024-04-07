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
public class AdministratorControllerV2Tests {
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
    public void testGetMeAdminInfo_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/me/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetAdminByUsername_Unauthorized() throws Exception {
        String username = "admin1";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/name" + username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetAdminByNameRegex_Unauthorized() throws Exception {
        String username = "admin1";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/rname/adminRegex?" + username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testCreateAdmin_Unauthorized() throws Exception {
        String adminID = "111";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/add/" + adminID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdateAdmin_Unauthorized() throws Exception {
        String adminID = "111";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/update/" + adminID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
    }

    @Test
    public void testPartiallyUpdateAdmin_Unauthorized() throws Exception {
        String adminID = "111";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/update/part/" + adminID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteAdmin_Unauthorized() throws Exception {
        String adminID = "111";
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/v2/del/" + adminID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
    }
}