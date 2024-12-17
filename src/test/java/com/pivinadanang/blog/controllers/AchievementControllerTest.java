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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}