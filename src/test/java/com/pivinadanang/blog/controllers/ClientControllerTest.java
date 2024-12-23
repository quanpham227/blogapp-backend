package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.ClientController;
import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.client.IClientService;
import com.pivinadanang.blog.ultils.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

public class ClientControllerTest {

    @Mock
    private IClientService clientService;

    @Mock
    private LocalizationUtils localizationUtils;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllClients() {
        List<ClientResponse> clients = Arrays.asList(new ClientResponse(), new ClientResponse());

        when(clientService.getAllClients()).thenReturn(clients);
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_CLIENT_SUCCESSFULLY)).thenReturn("Get clients successfully");

        ResponseEntity<ResponseObject> responseEntity = clientController.getAllClients();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get clients successfully", responseEntity.getBody().getMessage());
        assertEquals(clients, responseEntity.getBody().getData());
    }

    @Test
    public void testGetClientById() throws Exception {
        Long clientId = 1L;
        ClientEntity clientEntity = new ClientEntity();

        when(clientService.findById(clientId)).thenReturn(clientEntity);

        ResponseEntity<ResponseObject> responseEntity = clientController.getClientById(clientId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get client information successfully", responseEntity.getBody().getMessage());
        assertEquals(clientEntity, responseEntity.getBody().getData());
    }

    @Test
    public void testGetClientByIdNotFound() throws Exception {
        Long clientId = 1L;

        when(clientService.findById(clientId)).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = clientController.getClientById(clientId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Client not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testInsertClient() throws Exception {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("New Client");

        when(clientService.exitsByName(clientDTO.getName())).thenReturn(false);
        ClientResponse clientResponse = new ClientResponse();
        when(clientService.createClient(clientDTO)).thenReturn(clientResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CLIENT_SUCCESSFULLY)).thenReturn("Insert client successfully");

        ResponseEntity<ResponseObject> responseEntity = clientController.insertClient(clientDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Insert client successfully", responseEntity.getBody().getMessage());
        assertEquals(clientResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testInsertClientAlreadyExists() throws Exception {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Existing Client");

        when(clientService.exitsByName(clientDTO.getName())).thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.CLIENT_ALREADY_EXISTS)).thenReturn("Client already exists");

        ResponseEntity<ResponseObject> responseEntity = clientController.insertClient(clientDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Client already exists", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUpdateClient() throws Exception {
        Long id = 1L;
        ClientDTO clientDTO = new ClientDTO();
        ClientResponse clientResponse = new ClientResponse();

        when(clientService.updateClient(id, clientDTO)).thenReturn(clientResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CLIENT_SUCCESSFULLY, id)).thenReturn("Update client successfully");

        ResponseEntity<ResponseObject> responseEntity = clientController.updateClient(clientDTO, id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update client successfully", responseEntity.getBody().getMessage());
        assertEquals(clientResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testUpdateClientNotFound() throws Exception {
        Long id = 1L;
        ClientDTO clientDTO = new ClientDTO();

        when(clientService.updateClient(id, clientDTO)).thenThrow(new Exception("Client not found"));

        Exception exception = assertThrows(Exception.class, () -> {
            clientController.updateClient(clientDTO, id);
        });

        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    public void testDeleteClient() throws Exception {
        Long id = 1L;

        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_SUCCESSFULLY, id)).thenReturn("Delete client successfully");

        ResponseEntity<ResponseObject> responseEntity = clientController.deleteClient(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete client successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteClientNotFound() throws Exception {
        Long id = 1L;

        doThrow(new Exception("Client not found")).when(clientService).deleteClients(id);

        Exception exception = assertThrows(Exception.class, () -> {
            clientController.deleteClient(id);
        });

        assertEquals("Client not found", exception.getMessage());
    }
    @Test
    public void testGetAllClientsEmptyList() {
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_CLIENT_SUCCESSFULLY)).thenReturn("Get clients successfully");

        ResponseEntity<ResponseObject> responseEntity = clientController.getAllClients();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get clients successfully", responseEntity.getBody().getMessage());
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }
    @Test
    public void testInsertClientInvalidData() throws Exception {
        ClientDTO clientDTO = new ClientDTO(); // Empty name or missing data

        // Simulate validation errors
        BindingResult bindingResult = new BeanPropertyBindingResult(clientDTO, "clientDTO");
        bindingResult.addError(new FieldError("clientDTO", "name", "Name is required"));

        // Mock the service to handle the validation error
        when(clientService.createClient(clientDTO)).thenThrow(new Exception("Validation failed"));

        Exception exception = assertThrows(Exception.class, () -> {
            clientController.insertClient(clientDTO);
        });

        assertEquals("Validation failed", exception.getMessage());
    }

}