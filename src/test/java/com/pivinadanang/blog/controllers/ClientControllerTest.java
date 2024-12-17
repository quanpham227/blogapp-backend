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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    public void testDeleteClient() throws Exception {
        Long id = 1L;

        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_SUCCESSFULLY, id)).thenReturn("Delete client successfully");

        ResponseEntity<ResponseObject> responseEntity = clientController.deleteClient(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete client successfully", responseEntity.getBody().getMessage());
    }
}