package com.pivinadanang.blog.services.achievement;


import com.pivinadanang.blog.dtos.AchievementDTO;
import com.pivinadanang.blog.models.AchievementEntity;
import com.pivinadanang.blog.repositories.AchievementRepository;
import com.pivinadanang.blog.responses.achievement.AchievementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementService achievementService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllAchievementsForAdmin() {
        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.findAll()).thenReturn(Collections.singletonList(achievementEntity));

        // Call the service method
        List<AchievementResponse> achievements = achievementService.getAllAchievementsForAdmin();

        // Assertions
        assertEquals(1, achievements.size());
        AchievementResponse achievementResponse = achievements.get(0);
        assertEquals(1L, achievementResponse.getId());
        assertEquals("Achievement 1", achievementResponse.getTitle());
        assertEquals(100, achievementResponse.getValue());
        assertEquals("Description 1", achievementResponse.getDescription());
        assertTrue(achievementResponse.getIsActive());
    }

    @Test
    public void testGetAllAchievementsForUser() {
        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.findAllByIsActiveTrue()).thenReturn(Collections.singletonList(achievementEntity));

        // Call the service method
        List<AchievementResponse> achievements = achievementService.getAllAchievementsForUser();

        // Assertions
        assertEquals(1, achievements.size());
        AchievementResponse achievementResponse = achievements.get(0);
        assertEquals(1L, achievementResponse.getId());
        assertEquals("Achievement 1", achievementResponse.getTitle());
        assertEquals(100, achievementResponse.getValue());
        assertEquals("Description 1", achievementResponse.getDescription());
        assertTrue(achievementResponse.getIsActive());
    }

    @Test
    public void testAddAchievement_Success() throws Exception {
        // Mock AchievementDTO
        AchievementDTO achievementDTO = AchievementDTO.builder()
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.exitstsByTitle("Achievement 1")).thenReturn(false);
        when(achievementRepository.save(any(AchievementEntity.class))).thenReturn(achievementEntity);

        // Call the service method
        AchievementResponse achievementResponse = achievementService.addAchievement(achievementDTO);

        // Assertions
        assertEquals(1L, achievementResponse.getId());
        assertEquals("Achievement 1", achievementResponse.getTitle());
        assertEquals(100, achievementResponse.getValue());
        assertEquals("Description 1", achievementResponse.getDescription());
        assertTrue(achievementResponse.getIsActive());
    }

    @Test
    public void testAddAchievement_TitleExists() {
        // Mock AchievementDTO
        AchievementDTO achievementDTO = AchievementDTO.builder()
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.exitstsByTitle("Achievement 1")).thenReturn(true);

        // Call the service method and expect an exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            achievementService.addAchievement(achievementDTO);
        });

        // Assertions
        assertEquals("Title must be unique", exception.getMessage());
    }

    @Test
    public void testUpdateAchievement_Success() throws Exception {
        // Mock AchievementDTO
        AchievementDTO achievementDTO = AchievementDTO.builder()
                .title("Updated Achievement")
                .value(200)
                .description("Updated Description")
                .isActive(false)
                .build();

        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.findById(1L)).thenReturn(Optional.of(achievementEntity));
        when(achievementRepository.exitstsByTitle("Updated Achievement")).thenReturn(false);
        when(achievementRepository.save(any(AchievementEntity.class))).thenReturn(achievementEntity);

        // Call the service method
        AchievementResponse achievementResponse = achievementService.updateAchievement(1L, achievementDTO);

        // Assertions
        assertEquals(1L, achievementResponse.getId());
        assertEquals("Updated Achievement", achievementResponse.getTitle());
        assertEquals(200, achievementResponse.getValue());
        assertEquals("Updated Description", achievementResponse.getDescription());
        assertFalse(achievementResponse.getIsActive());
    }

    @Test
    public void testUpdateAchievement_NotFound() {
        // Mock AchievementDTO
        AchievementDTO achievementDTO = AchievementDTO.builder()
                .title("Updated Achievement")
                .value(200)
                .description("Updated Description")
                .isActive(false)
                .build();

        when(achievementRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(Exception.class, () -> {
            achievementService.updateAchievement(1L, achievementDTO);
        });

        // Assertions
        assertEquals("Achievement not found", exception.getMessage());
    }

    @Test
    public void testUpdateAchievement_TitleExists() {
        // Mock AchievementDTO
        AchievementDTO achievementDTO = AchievementDTO.builder()
                .title("Updated Achievement")
                .value(200)
                .description("Updated Description")
                .isActive(false)
                .build();

        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.findById(1L)).thenReturn(Optional.of(achievementEntity));
        when(achievementRepository.exitstsByTitle("Updated Achievement")).thenReturn(true);

        // Call the service method and expect an exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            achievementService.updateAchievement(1L, achievementDTO);
        });

        // Assertions
        assertEquals("Achievement already exists", exception.getMessage());
    }

    @Test
    public void testDeleteAchievement_Success() throws Exception {
        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.findById(1L)).thenReturn(Optional.of(achievementEntity));

        // Call the service method
        achievementService.deleteAchievement(1L);

        // Verify the repository method was called
        verify(achievementRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteAchievement_NotFound() {
        when(achievementRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(Exception.class, () -> {
            achievementService.deleteAchievement(1L);
        });

        // Assertions
        assertEquals("Achievement not found", exception.getMessage());
    }

    @Test
    public void testGetAchievementById_Success() throws Exception {
        // Mock AchievementEntity
        AchievementEntity achievementEntity = AchievementEntity.builder()
                .id(1L)
                .title("Achievement 1")
                .value(100)
                .description("Description 1")
                .isActive(true)
                .build();

        when(achievementRepository.findById(1L)).thenReturn(Optional.of(achievementEntity));

        // Call the service method
        AchievementResponse achievementResponse = achievementService.getAchievementById(1L);

        // Assertions
        assertEquals(1L, achievementResponse.getId());
        assertEquals("Achievement 1", achievementResponse.getTitle());
        assertEquals(100, achievementResponse.getValue());
        assertEquals("Description 1", achievementResponse.getDescription());
        assertTrue(achievementResponse.getIsActive());
    }

    @Test
    public void testGetAchievementById_NotFound() {
        when(achievementRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(Exception.class, () -> {
            achievementService.getAchievementById(1L);
        });

        // Assertions
        assertEquals("Achievement not found", exception.getMessage());
    }

    @Test
    public void testExistsAchievementByTitle() {
        when(achievementRepository.exitstsByTitle("Achievement 1")).thenReturn(true);

        // Call the service method
        boolean exists = achievementService.existsAchievementByTitle("Achievement 1");

        // Assertions
        assertTrue(exists);
    }
}