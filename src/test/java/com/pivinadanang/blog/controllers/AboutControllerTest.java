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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
