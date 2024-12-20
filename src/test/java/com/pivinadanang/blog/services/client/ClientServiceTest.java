package com.pivinadanang.blog.services.client;


import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.repositories.ClientRepository;
import com.pivinadanang.blog.repositories.ImageRepository;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.ultils.HtmlSanitizer;
import com.pivinadanang.blog.ultils.HtmlSanitizerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ClientService clientService;
    @Mock
    private HtmlSanitizerWrapper htmlSanitizerWrapper;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExitsByName() {
        when(clientRepository.existsByName("Client 1")).thenReturn(true);

        // Call the service method
        boolean exists = clientService.exitsByName("Client 1");

        // Assertions
        assertTrue(exists);
    }

    @Test
    public void testFindById_Success() throws Exception {
        // Mock ClientEntity
        ClientEntity clientEntity = ClientEntity.builder()
                .id(1L)
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));

        // Call the service method
        ClientEntity client = clientService.findById(1L);

        // Assertions
        assertEquals(1L, client.getId());
        assertEquals("Client 1", client.getName());
        assertEquals("Description 1", client.getDescription());
        assertEquals("logo.png", client.getLogo());
        assertEquals("public-id-1", client.getPublicId());
    }

    @Test
    public void testFindById_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            clientService.findById(1L);
        });

        // Assertions
        assertEquals("Cannot find client with id 1", exception.getMessage());
    }

    @Test
    public void testCreateClient_Success() throws IOException {
        // Mock ClientDTO
        ClientDTO clientDTO = ClientDTO.builder()
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        // Mock ImageEntity
        ImageEntity imageEntity = ImageEntity.builder()
                .publicId("public-id-1")
                .isUsed(false)
                .usageCount(0)
                .build();

        // Mock ClientEntity
        ClientEntity clientEntity = ClientEntity.builder()
                .id(1L)
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        // Use doReturn().when() for HtmlSanitizerWrapper.sanitize()
        when(htmlSanitizerWrapper.sanitize("Description 1")).thenReturn("Description 1");

        when(imageRepository.findByPublicId("public-id-1")).thenReturn(Optional.of(imageEntity));
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);

        // Call the service method
        ClientResponse clientResponse = clientService.createClient(clientDTO);

        // Assertions
        assertEquals(1L, clientResponse.getId());
        assertEquals("Client 1", clientResponse.getName());
        assertEquals("Description 1", clientResponse.getDescription());
        assertEquals("logo.png", clientResponse.getLogo());
        assertEquals("public-id-1", clientResponse.getPublicId());
    }

    @Test
    public void testCreateClient_ImageNotFound() {
        // Mock ClientDTO
        ClientDTO clientDTO = ClientDTO.builder()
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        when(imageRepository.findByPublicId("public-id-1")).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            clientService.createClient(clientDTO);
        });

        // Assertions
        assertEquals("Image not found", exception.getMessage());
    }

    @Test
    public void testGetAllClients() {
        // Mock ClientEntity
        ClientEntity clientEntity = ClientEntity.builder()
                .id(1L)
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        when(clientRepository.findAll()).thenReturn(Collections.singletonList(clientEntity));

        // Call the service method
        List<ClientResponse> clients = clientService.getAllClients();

        // Assertions
        assertEquals(1, clients.size());
        ClientResponse clientResponse = clients.get(0);
        assertEquals(1L, clientResponse.getId());
        assertEquals("Client 1", clientResponse.getName());
        assertEquals("Description 1", clientResponse.getDescription());
        assertEquals("logo.png", clientResponse.getLogo());
        assertEquals("public-id-1", clientResponse.getPublicId());
    }

    @Test
    public void testUpdateClient_Success() throws DataNotFoundException, IOException {
        // Mock ClientDTO
        ClientDTO clientDTO = ClientDTO.builder()
                .name("Updated Client")
                .description("Updated Description")
                .logo("updated-logo.png")
                .publicId("updated-public-id")
                .build();

        // Mock ClientEntity
        ClientEntity clientEntity = ClientEntity.builder()
                .id(1L)
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        // Mock ImageEntity
        ImageEntity currentImage = ImageEntity.builder()
                .publicId("public-id-1")
                .isUsed(true)
                .usageCount(1)
                .build();

        ImageEntity newImage = ImageEntity.builder()
                .publicId("updated-public-id")
                .isUsed(false)
                .usageCount(0)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        // Use doReturn().when() for HtmlSanitizerWrapper.sanitize()
        when(htmlSanitizerWrapper.sanitize("Updated Description")).thenReturn("Updated Description");

        when(imageRepository.findByPublicId("public-id-1")).thenReturn(Optional.of(currentImage));
        when(imageRepository.findByPublicId("updated-public-id")).thenReturn(Optional.of(newImage));
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);

        // Call the service method
        ClientResponse clientResponse = clientService.updateClient(1L, clientDTO);

        // Assertions
        assertEquals(1L, clientResponse.getId());
        assertEquals("Updated Client", clientResponse.getName());
        assertEquals("Updated Description", clientResponse.getDescription());
        assertEquals("updated-logo.png", clientResponse.getLogo());
        assertEquals("updated-public-id", clientResponse.getPublicId());
    }
    @Test
    public void testUpdateClient_NotFound() {
        // Mock ClientDTO
        ClientDTO clientDTO = ClientDTO.builder()
                .name("Updated Client")
                .description("Updated Description")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            clientService.updateClient(1L, clientDTO);
        });

        // Assertions
        assertEquals("Cannot find client with id 1", exception.getMessage());
    }

    @Test
    public void testUpdateClient_NameExists() {
        // Mock ClientDTO
        ClientDTO clientDTO = ClientDTO.builder()
                .name("Updated Client")
                .description("Updated Description")
                .build();

        // Mock ClientEntity
        ClientEntity clientEntity = ClientEntity.builder()
                .id(1L)
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(clientRepository.existsByName("Updated Client")).thenReturn(true);

        // Call the service method and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.updateClient(1L, clientDTO);
        });

        // Assertions
        assertEquals("Name already exists", exception.getMessage());
    }

    @Test
    public void testDeleteClients_Success() throws Exception {
        // Mock ClientEntity
        ClientEntity clientEntity = ClientEntity.builder()
                .id(1L)
                .name("Client 1")
                .description("Description 1")
                .logo("logo.png")
                .publicId("public-id-1")
                .build();

        // Mock ImageEntity
        ImageEntity imageEntity = ImageEntity.builder()
                .publicId("public-id-1")
                .isUsed(true)
                .usageCount(1)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(imageRepository.findByPublicId("public-id-1")).thenReturn(Optional.of(imageEntity));

        // Call the service method
        clientService.deleteClients(1L);

        // Verify the repository method was called
        verify(clientRepository, times(1)).delete(clientEntity);
        assertEquals(0, imageEntity.getUsageCount());
        assertFalse(imageEntity.getIsUsed());
    }

    @Test
    public void testDeleteClients_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            clientService.deleteClients(1L);
        });

        // Assertions
        assertEquals("Cannot find client with id 1", exception.getMessage());
    }
}