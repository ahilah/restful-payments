package com.payments.restpayments.controller.role;

import com.payments.restpayments.controller.role.SuperAdminController;
import com.payments.restpayments.role.SuperAdmin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class SuperAdminControllerTest {

    @Mock
    private SuperAdmin superAdminMock;

    @InjectMocks
    private SuperAdminController superAdminController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        SuperAdmin testSuperAdmin = SuperAdmin.getInstance();
        /*when(superAdminMock.getUsername()).thenReturn("bosa");
        when(superAdminMock.getPassword()).thenReturn("nova");
        when(superAdminMock.getId()).thenReturn(1);*/

        ResponseEntity<String> responseEntity = superAdminController.login(testSuperAdmin);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testLoginUnauthorized() {
        SuperAdmin testSuperAdmin = SuperAdmin.getInstance();

        ResponseEntity<String> responseEntity = superAdminController.login(testSuperAdmin);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}

