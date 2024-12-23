package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.AchievementController;
import com.pivinadanang.blog.dtos.AchievementDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.achievement.AchievementResponse;
import com.pivinadanang.blog.services.achievement.IAchievementService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class AchievementControllerTest {

    @Mock
    private IAchievementService achievementService;

    @Mock
    private LocalizationUtils localizationUtils;

    @InjectMocks
    private AchievementController achievementController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsertAchievement() throws Exception {
        AchievementDTO achievementDTO = new AchievementDTO();
        achievementDTO.setTitle("New Achievement");

        when(achievementService.existsAchievementByTitle(achievementDTO.getTitle())).thenReturn(false);
        AchievementResponse achievementResponse = new AchievementResponse();
        when(achievementService.addAchievement(achievementDTO)).thenReturn(achievementResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_ACHIEVEMENT_SUCCESSFULLY)).thenReturn("Insert achievement successfully");

        ResponseEntity<ResponseObject> responseEntity = achievementController.insertAchievement(achievementDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Insert achievement successfully", responseEntity.getBody().getMessage());
        assertEquals(achievementResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testUpdateAchievement() throws Exception {
        Long id = 1L;
        AchievementDTO achievementDTO = new AchievementDTO();
        AchievementResponse achievementResponse = new AchievementResponse();

        when(achievementService.updateAchievement(id, achievementDTO)).thenReturn(achievementResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_ACHIEVEMENT_SUCCESSFULLY)).thenReturn("Update achievement successfully");

        ResponseEntity<ResponseObject> responseEntity = achievementController.updateAchievement(id, achievementDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update achievement successfully", responseEntity.getBody().getMessage());
        assertEquals(achievementResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testDeleteAchievement() throws Exception {
        Long id = 1L;

        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ACHIEVEMENT_SUCCESSFULLY)).thenReturn("Delete achievement successfully");

        ResponseEntity<ResponseObject> responseEntity = achievementController.deleteAchievement(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete achievement successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetAllAchievementsForAdmin() {
        List<AchievementResponse> achievements = Arrays.asList(new AchievementResponse(), new AchievementResponse());

        when(achievementService.getAllAchievementsForAdmin()).thenReturn(achievements);

        ResponseEntity<ResponseObject> responseEntity = achievementController.getAllAchievementsForAdmin();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get list of achievements successfully", responseEntity.getBody().getMessage());
        assertEquals(achievements, responseEntity.getBody().getData());
    }

    @Test
    public void testGetAllAchievementsForUser() {
        List<AchievementResponse> achievements = Arrays.asList(new AchievementResponse(), new AchievementResponse());

        when(achievementService.getAllAchievementsForUser()).thenReturn(achievements);

        ResponseEntity<ResponseObject> responseEntity = achievementController.getAllAchievementsForUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get list of achievements successfully", responseEntity.getBody().getMessage());
        assertEquals(achievements, responseEntity.getBody().getData());
    }
    @Test
    public void testInsertAchievementWithExistingTitle() throws Exception {
        AchievementDTO achievementDTO = new AchievementDTO();
        achievementDTO.setTitle("Existing Achievement");

        when(achievementService.existsAchievementByTitle(achievementDTO.getTitle())).thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.ACHIEVEMENT_ALREADY_EXISTS)).thenReturn("Achievement already exists");

        ResponseEntity<ResponseObject> responseEntity = achievementController.insertAchievement(achievementDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Achievement already exists", responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    public void testInsertAchievementThrowsException() throws Exception {
        AchievementDTO achievementDTO = new AchievementDTO();
        achievementDTO.setTitle("New Achievement");

        when(achievementService.existsAchievementByTitle(achievementDTO.getTitle())).thenReturn(false);
        when(achievementService.addAchievement(achievementDTO)).thenThrow(new Exception("Error inserting achievement"));

        Exception exception = assertThrows(Exception.class, () -> {
            achievementController.insertAchievement(achievementDTO);
        });

        assertEquals("Error inserting achievement", exception.getMessage());
    }

    @Test
    public void testUpdateAchievementNotFound() throws Exception {
        Long id = 1L;
        AchievementDTO achievementDTO = new AchievementDTO();

        when(achievementService.updateAchievement(id, achievementDTO)).thenThrow(new Exception("Achievement not found"));

        Exception exception = assertThrows(Exception.class, () -> {
            achievementController.updateAchievement(id, achievementDTO);
        });

        assertEquals("Achievement not found", exception.getMessage());
    }

    @Test
    public void testDeleteAchievementNotFound() throws Exception {
        Long id = 1L;

        doThrow(new Exception("Achievement not found")).when(achievementService).deleteAchievement(id);

        Exception exception = assertThrows(Exception.class, () -> {
            achievementController.deleteAchievement(id);
        });

        assertEquals("Achievement not found", exception.getMessage());
    }

    @Test
    public void testGetAllAchievementsForAdminReturnsEmptyList() {
        when(achievementService.getAllAchievementsForAdmin()).thenReturn(Arrays.asList());

        ResponseEntity<ResponseObject> responseEntity = achievementController.getAllAchievementsForAdmin();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get list of achievements successfully", responseEntity.getBody().getMessage());
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }

    @Test
    public void testGetAllAchievementsForUserReturnsEmptyList() {
        when(achievementService.getAllAchievementsForUser()).thenReturn(Arrays.asList());

        ResponseEntity<ResponseObject> responseEntity = achievementController.getAllAchievementsForUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get list of achievements successfully", responseEntity.getBody().getMessage());
        assertTrue(((List<?>) responseEntity.getBody().getData()).isEmpty());
    }
    @Test
    public void testInsertAchievementWithInvalidData() throws Exception {
        AchievementDTO achievementDTO = new AchievementDTO(); // Không có tiêu đề

        // Simulate validation errors
        BindingResult bindingResult = new BeanPropertyBindingResult(achievementDTO, "achievementDTO");
        bindingResult.addError(new FieldError("achievementDTO", "title", "Title is required"));

        // Mock the service to handle the validation error
        when(achievementService.addAchievement(achievementDTO)).thenThrow(new Exception("Validation failed"));

        Exception exception = assertThrows(Exception.class, () -> {
            achievementController.insertAchievement(achievementDTO);
        });

        assertTrue(exception.getMessage().contains("Validation failed"));
    }
    @Test
    public void testUpdateAchievementWithBoundaryId() throws Exception {
        Long id = Long.MAX_VALUE;
        AchievementDTO achievementDTO = new AchievementDTO();
        achievementDTO.setTitle("Boundary Test");

        AchievementResponse achievementResponse = new AchievementResponse();
        when(achievementService.updateAchievement(id, achievementDTO)).thenReturn(achievementResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_ACHIEVEMENT_SUCCESSFULLY))
                .thenReturn("Update achievement successfully");

        ResponseEntity<ResponseObject> responseEntity = achievementController.updateAchievement(id, achievementDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update achievement successfully", responseEntity.getBody().getMessage());
    }
    @Test
    public void testGetAllAchievementsForAdminWithLargeDataSet() {
        List<AchievementResponse> largeAchievements = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeAchievements.add(new AchievementResponse());
        }

        when(achievementService.getAllAchievementsForAdmin()).thenReturn(largeAchievements);

        ResponseEntity<ResponseObject> responseEntity = achievementController.getAllAchievementsForAdmin();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(10000, ((List<?>) responseEntity.getBody().getData()).size());
    }

}