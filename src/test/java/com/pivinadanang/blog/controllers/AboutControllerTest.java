package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.controller.AboutController;
import com.pivinadanang.blog.dtos.AboutDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.about.AboutResponse;
import com.pivinadanang.blog.services.about.IAboutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
public class AboutControllerTest {
    @Mock
    private IAboutService aboutService;

    @InjectMocks
    private AboutController aboutController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAbout() throws Exception {
        AboutResponse aboutResponse = new AboutResponse();
        when(aboutService.getAbout()).thenReturn(aboutResponse);

        ResponseEntity<ResponseObject> responseEntity = aboutController.getAbout();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get about information successfully", responseEntity.getBody().getMessage());
        assertEquals(aboutResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testUpdateAbout() throws Exception {
        Long id = 1L;
        AboutDTO aboutDTO = new AboutDTO();
        AboutResponse aboutResponse = new AboutResponse();
        when(aboutService.updateAbout(id, aboutDTO)).thenReturn(aboutResponse);

        ResponseEntity<ResponseObject> responseEntity = aboutController.updateAbout(id, aboutDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update about information successfully", responseEntity.getBody().getMessage());
        assertEquals(aboutResponse, responseEntity.getBody().getData());
    }
    @Test
    public void testGetAboutThrowsException() throws Exception {
        when(aboutService.getAbout()).thenThrow(new Exception("Error fetching about"));

        Exception exception = assertThrows(Exception.class, () -> {
            aboutController.getAbout();
        });

        assertEquals("Error fetching about", exception.getMessage());
    }
    @Test
    public void testUpdateAboutThrowsException() throws Exception {
        Long id = 1L;
        AboutDTO aboutDTO = new AboutDTO();

        when(aboutService.updateAbout(id, aboutDTO)).thenThrow(new Exception("Error updating about"));

        Exception exception = assertThrows(Exception.class, () -> {
            aboutController.updateAbout(id, aboutDTO);
        });

        assertEquals("Error updating about", exception.getMessage());
    }
    @Test
    public void testUpdateAboutWithInvalidData() throws Exception {
        Long id = 1L;
        AboutDTO aboutDTO = new AboutDTO(); // Điền thông tin không hợp lệ

        // Simulate validation errors
        BindingResult bindingResult = new BeanPropertyBindingResult(aboutDTO, "aboutDTO");
        bindingResult.addError(new FieldError("aboutDTO", "field", "Validation failed"));

        // Mock the service to throw an exception when validation fails
        when(aboutService.updateAbout(id, aboutDTO)).thenThrow(new Exception("Validation failed"));

        Exception exception = assertThrows(Exception.class, () -> {
            aboutController.updateAbout(id, aboutDTO);
        });

        assertTrue(exception.getMessage().contains("Validation failed"));
    }
    @Test
    public void testGetAboutReturnsNull() throws Exception {
        when(aboutService.getAbout()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = aboutController.getAbout();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody().getData());
        assertEquals("Get about information successfully", responseEntity.getBody().getMessage());
    }
    @Test
    public void testUpdateAboutNotFound() throws Exception {
        Long id = 1L;
        AboutDTO aboutDTO = new AboutDTO();

        when(aboutService.updateAbout(id, aboutDTO)).thenThrow(new Exception("Resource not found"));

        Exception exception = assertThrows(Exception.class, () -> {
            aboutController.updateAbout(id, aboutDTO);
        });

        assertEquals("Resource not found", exception.getMessage());
    }


    @Test
    public void testUpdateAboutWithEmptyDTO() throws Exception {
        Long id = 1L;
        AboutDTO aboutDTO = new AboutDTO(); // Empty DTO

        when(aboutService.updateAbout(id, aboutDTO)).thenReturn(new AboutResponse());

        ResponseEntity<ResponseObject> responseEntity = aboutController.updateAbout(id, aboutDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update about information successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUpdateAboutWithPartialDTO() throws Exception {
        Long id = 1L;
        AboutDTO aboutDTO = new AboutDTO();
        aboutDTO.setTitle("New Title"); // Partial update

        AboutResponse aboutResponse = new AboutResponse();
        when(aboutService.updateAbout(id, aboutDTO)).thenReturn(aboutResponse);

        ResponseEntity<ResponseObject> responseEntity = aboutController.updateAbout(id, aboutDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update about information successfully", responseEntity.getBody().getMessage());
        assertEquals(aboutResponse, responseEntity.getBody().getData());
    }

}
