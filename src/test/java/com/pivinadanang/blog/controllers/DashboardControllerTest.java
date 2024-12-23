package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.controller.DashboardController;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.dashboard.DashboardResponse;
import com.pivinadanang.blog.services.dashboard.IDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
public class DashboardControllerTest {
    private MockMvc mockMvc;

    @Mock
    private IDashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();

    }

    @Test
    public void testGetDashboardData() {
        DashboardResponse dashboardResponse = new DashboardResponse();
        when(dashboardService.getDashboardData()).thenReturn(dashboardResponse);

        ResponseEntity<ResponseObject> responseEntity = dashboardController.getDashboardData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get dashboard data successfully", responseEntity.getBody().getMessage());
        assertEquals(dashboardResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testGetDashboardData_Success() {
        DashboardResponse dashboardResponse = new DashboardResponse();
        when(dashboardService.getDashboardData()).thenReturn(dashboardResponse);

        ResponseEntity<ResponseObject> responseEntity = dashboardController.getDashboardData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get dashboard data successfully", responseEntity.getBody().getMessage());
        assertEquals(dashboardResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testGetDashboardData_NullResponse() {
        when(dashboardService.getDashboardData()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = dashboardController.getDashboardData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get dashboard data successfully", responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    public void testGetDashboardData_ServiceException() {
        when(dashboardService.getDashboardData()).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ResponseObject> responseEntity;
        try {
            responseEntity = dashboardController.getDashboardData();
        } catch (Exception ex) {
            assertEquals("Service error", ex.getMessage());
        }
    }

    @Test
    public void testGetDashboardData_EmptyResponse() {
        DashboardResponse dashboardResponse = new DashboardResponse(); // No data inside
        when(dashboardService.getDashboardData()).thenReturn(dashboardResponse);

        ResponseEntity<ResponseObject> responseEntity = dashboardController.getDashboardData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get dashboard data successfully", responseEntity.getBody().getMessage());
        assertEquals(dashboardResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testGetDashboardData_Performance() {
        DashboardResponse dashboardResponse = new DashboardResponse();
        when(dashboardService.getDashboardData()).thenReturn(dashboardResponse);

        long startTime = System.currentTimeMillis();
        ResponseEntity<ResponseObject> responseEntity = dashboardController.getDashboardData();
        long endTime = System.currentTimeMillis();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue((endTime - startTime) < 200); // Less than 200ms
    }

    @Test
    public void testGetDashboardData_ServiceNotConfigured() {
        DashboardController controllerWithNullService = new DashboardController(null);

        try {
            controllerWithNullService.getDashboardData();
        } catch (Exception ex) {
            assertTrue(ex instanceof NullPointerException);
        }
    }


}
