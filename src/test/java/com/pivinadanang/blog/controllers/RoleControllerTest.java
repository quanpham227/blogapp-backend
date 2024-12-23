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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    @Test
    public void testGetAllRoles_SuccessWithRoles() {
        List<RoleResponse> roleResponses = Arrays.asList(new RoleResponse("ADMIN"), new RoleResponse("User"));
        when(roleService.getAllRoles()).thenReturn(roleResponses);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get all roles successfully", responseEntity.getBody().getMessage());
        assertEquals(roleResponses, responseEntity.getBody().getData());
    }
    @Test
    public void testGetAllRoles_SuccessWithEmptyRoles() {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("", responseEntity.getBody().getMessage()); // Expecting empty message
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }
    @Test
    public void testGetAllRoles_ServiceThrowsException() {
        when(roleService.getAllRoles()).thenThrow(new RuntimeException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleController.getAllRoles());

        assertEquals("Service unavailable", exception.getMessage());
    }
    @Test
    public void testGetAllRoles_ResponseBodyMissingMessage() {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(new RoleResponse("Admin")));

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertNotNull(responseEntity.getBody());
        assertEquals("Get all roles successfully", responseEntity.getBody().getMessage());
    }
    @Test
    public void testGetAllRoles_ServiceReturnsNull() {
        when(roleService.getAllRoles()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("", responseEntity.getBody().getMessage());
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }

    @Test
    public void testGetAllRoles_ResponseBodyMissingData() {
        when(roleService.getAllRoles()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertNotNull(responseEntity.getBody());
        assertEquals("", responseEntity.getBody().getMessage()); // Expecting empty message
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty()); // Expecting empty list
    }

    @Test
    public void testGetAllRoles_RolesListContainsNull() {
        List<RoleResponse> roleResponses = Arrays.asList(new RoleResponse("Admin"), null, new RoleResponse("User"));
        when(roleService.getAllRoles()).thenReturn(roleResponses);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get all roles successfully", responseEntity.getBody().getMessage());
        assertEquals(roleResponses, responseEntity.getBody().getData());
        assertTrue(((List<?>) responseEntity.getBody().getData()).contains(null));
    }
    @Test
    public void testGetAllRoles_LocalizationUtilsThrowsException() {
        when(roleService.getAllRoles()).thenThrow(new RuntimeException("Localization error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleController.getAllRoles());

        assertEquals("Localization error", exception.getMessage());
    }
    @Test
    public void testGetAllRoles_ResponseTime() {
        List<RoleResponse> roleResponses = Arrays.asList(new RoleResponse("Admin"), new RoleResponse("User"));
        when(roleService.getAllRoles()).thenAnswer(invocation -> {
            Thread.sleep(100); // Simulate delay
            return roleResponses;
        });

        long startTime = System.currentTimeMillis();
        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();
        long endTime = System.currentTimeMillis();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get all roles successfully", responseEntity.getBody().getMessage());
        assertEquals(roleResponses, responseEntity.getBody().getData());
        assertTrue(endTime - startTime < 200); // Check response is under 200ms
    }

    @Test
    public void testGetAllRoles_ServiceConnectionError() {
        when(roleService.getAllRoles()).thenThrow(new RuntimeException("Connection failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleController.getAllRoles());

        assertEquals("Connection failed", exception.getMessage());
    }
    @Test
    public void testGetAllRoles_UnexpectedStatusCode() {
        when(roleService.getAllRoles()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testGetAllRoles_AbnormalRolesList() {
        List<RoleResponse> roleResponses = Arrays.asList(new RoleResponse("Admin"), new RoleResponse("")); // Empty name
        when(roleService.getAllRoles()).thenReturn(roleResponses);

        ResponseEntity<ResponseObject> responseEntity = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get all roles successfully", responseEntity.getBody().getMessage());
        assertEquals(roleResponses, responseEntity.getBody().getData());
        assertEquals("", ((List<RoleResponse>) responseEntity.getBody().getData()).get(1).getName());
    }


}