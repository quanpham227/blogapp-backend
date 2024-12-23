package com.pivinadanang.blog.controllers;


import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.configurations.SecurityConfig;
import com.pivinadanang.blog.controller.SlideController;
import com.pivinadanang.blog.dtos.SlideDTO;
import com.pivinadanang.blog.dtos.SlideOrderDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.slide.SlideResponse;
import com.pivinadanang.blog.services.slide.ISlideService;
import com.pivinadanang.blog.ultils.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class SlideControllerTest {

    @Mock
    private ISlideService slideService;

    @Mock
    private LocalizationUtils localizationUtils;

    @InjectMocks
    private SlideController slideController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSlides() {
        List<SlideResponse> slideResponses = Arrays.asList(new SlideResponse(), new SlideResponse());

        when(slideService.getAllSlides()).thenReturn(slideResponses);
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_SLIDE_SUCCESSFULLY)).thenReturn("Get slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.getAllSlides();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get slide successfully", responseEntity.getBody().getMessage());
        assertEquals(slideResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testFindAllByStatusTrue() {
        List<SlideResponse> slideResponses = Arrays.asList(new SlideResponse(), new SlideResponse());

        when(slideService.findAllByStatusTrue()).thenReturn(slideResponses);
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_SLIDE_SUCCESSFULLY)).thenReturn("Get slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.findAllByStatusTrue();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get slide successfully", responseEntity.getBody().getMessage());
        assertEquals(slideResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testGetSlideById() throws Exception {
        Long slideId = 1L;
        SlideResponse slideResponse = new SlideResponse();

        when(slideService.findById(slideId)).thenReturn(slideResponse);

        ResponseEntity<ResponseObject> responseEntity = slideController.getSlideById(slideId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get slide information successfully", responseEntity.getBody().getMessage());
        assertEquals(slideResponse, responseEntity.getBody().getData());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testInsertSlide() throws Exception {
        SlideDTO slideDTO = new SlideDTO();
        slideDTO.setTitle("Test Slide");
        BindingResult result = mock(BindingResult.class);

        when(slideService.existsByTitle(slideDTO.getTitle())).thenReturn(false);
        when(slideService.createSlide(slideDTO)).thenReturn(new SlideResponse());
        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_SLIDE_SUCCESSFULLY)).thenReturn("Insert slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.insertSlide(slideDTO, result);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Insert slide successfully", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testUpdateSlide() throws Exception {
        Long slideId = 1L;
        SlideDTO slideDTO = new SlideDTO();
        SlideResponse slideResponse = new SlideResponse();
        BindingResult result = mock(BindingResult.class);

        when(slideService.updateSlide(slideId, slideDTO)).thenReturn(slideResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_SLIDE_SUCCESSFULLY, slideId)).thenReturn("Update slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.updateSlide(slideDTO, slideId, result);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update slide successfully", responseEntity.getBody().getMessage());
        assertEquals(slideResponse, responseEntity.getBody().getData());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testUpdateSlideOrder() throws Exception {
        List<SlideOrderDTO> slideOrderDTOs = Arrays.asList(new SlideOrderDTO(), new SlideOrderDTO());

        doNothing().when(slideService).updateSlideOrder(slideOrderDTOs);

        ResponseEntity<ResponseObject> responseEntity = slideController.updateSlideOrder(slideOrderDTOs);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update slides order successfully", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testDeleteSlide() throws Exception {
        Long slideId = 1L;

        doNothing().when(slideService).deleteSlide(slideId);
        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_SLIDE_SUCCESSFULLY, slideId)).thenReturn("Delete slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.deleteSlide(slideId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete slide successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testInsertSlideWithDuplicateTitle() throws Exception {
        SlideDTO slideDTO = new SlideDTO();
        slideDTO.setTitle("Duplicate Title");
        BindingResult result = mock(BindingResult.class);

        when(slideService.existsByTitle(slideDTO.getTitle())).thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_TITLE_ALREADY_EXISTS)).thenReturn("Slide title already exists");

        ResponseEntity<ResponseObject> responseEntity = slideController.insertSlide(slideDTO, result);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Slide title already exists", responseEntity.getBody().getMessage());
    }

    @Test
    public void testInsertSlideWithValidationErrors() throws Exception {
        SlideDTO slideDTO = new SlideDTO();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Arrays.asList(new FieldError("slideDTO", "title", "Title is required")));

        ResponseEntity<ResponseObject> responseEntity = slideController.insertSlide(slideDTO, result);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation errors", responseEntity.getBody().getMessage());
        assertTrue(responseEntity.getBody().getData() instanceof List);
        assertEquals(1, ((List<?>) responseEntity.getBody().getData()).size());
    }

    @Test
    public void testUpdateSlideWithValidationErrors() throws Exception {
        Long slideId = 1L;
        SlideDTO slideDTO = new SlideDTO();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Arrays.asList(new FieldError("slideDTO", "title", "Title is required")));

        ResponseEntity<ResponseObject> responseEntity = slideController.updateSlide(slideDTO, slideId, result);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation errors", responseEntity.getBody().getMessage());
        assertTrue(responseEntity.getBody().getData() instanceof List);
        assertEquals(1, ((List<?>) responseEntity.getBody().getData()).size());
    }

    @Test
    public void testUpdateSlideNonExistentId() throws Exception {
        Long slideId = 1L;
        SlideDTO slideDTO = new SlideDTO();
        BindingResult result = mock(BindingResult.class);

        when(slideService.updateSlide(slideId, slideDTO)).thenThrow(new RuntimeException("Slide not found"));
        when(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_NOT_FOUND, slideId)).thenReturn("Slide not found");

        ResponseEntity<ResponseObject> responseEntity = slideController.updateSlide(slideDTO, slideId, result);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Slide not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetSlideByIdNonExistentId() throws Exception {
        Long slideId = 1L;
        when(slideService.findById(slideId)).thenThrow(new RuntimeException("Slide not found"));
        when(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_NOT_FOUND, slideId)).thenReturn("Slide not found");

        ResponseEntity<ResponseObject> responseEntity = slideController.getSlideById(slideId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Slide not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteSlideNonExistentId() throws Exception {
        Long slideId = 1L;
        doThrow(new RuntimeException("Slide not found")).when(slideService).deleteSlide(slideId);
        when(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_NOT_FOUND, slideId)).thenReturn("Slide not found");

        ResponseEntity<ResponseObject> responseEntity = slideController.deleteSlide(slideId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Slide not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testFindAllByStatusTrueNoSlides() {
        when(slideService.findAllByStatusTrue()).thenReturn(Collections.emptyList());
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_SLIDE_SUCCESSFULLY)).thenReturn("Get slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.findAllByStatusTrue();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get slide successfully", responseEntity.getBody().getMessage());
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }

    @Test
    public void testGetAllSlidesNoSlides() {
        when(slideService.getAllSlides()).thenReturn(Collections.emptyList());
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_SLIDE_SUCCESSFULLY)).thenReturn("Get slide successfully");

        ResponseEntity<ResponseObject> responseEntity = slideController.getAllSlides();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get slide successfully", responseEntity.getBody().getMessage());
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }

    @Test
    public void testUpdateSlideOrderInvalidInput() throws Exception {
        List<SlideOrderDTO> slideOrderDTOs = Arrays.asList(new SlideOrderDTO(), new SlideOrderDTO());
        doThrow(new RuntimeException("Invalid input")).when(slideService).updateSlideOrder(slideOrderDTOs);
        when(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_INPUT)).thenReturn("Invalid input");

        ResponseEntity<ResponseObject> responseEntity = slideController.updateSlideOrder(slideOrderDTOs);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid input", responseEntity.getBody().getMessage());
    }




}