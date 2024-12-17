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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DashboardControllerTest {

    @Mock
    private IDashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
}