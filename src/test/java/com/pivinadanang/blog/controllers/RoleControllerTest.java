package com.pivinadanang.blog.controllers;


import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.RoleController;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.role.RoleResponse;
import com.pivinadanang.blog.services.role.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RoleControllerTest {

    @Mock
    private IRoleService roleService;

    @Mock
    private LocalizationUtils localizationUtils;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllRoles() {
        List<RoleResponse> roleResponses = Arrays.asList(new RoleResponse(), new RoleResponse());

        when(roleService.getAllRoles()).thenReturn(roleResponses);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get all roles successfully", responseEntity.getBody().getMessage());
        assertEquals(roleResponses, responseEntity.getBody().getData());
    }
}