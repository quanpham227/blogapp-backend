package com.pivinadanang.blog.repository;


import com.pivinadanang.blog.models.AchievementEntity;
import com.pivinadanang.blog.repositories.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
class AchievementRepositoryTest {

    @Autowired
    private AchievementRepository achievementRepository;

    @BeforeEach
    void setUp() {
        // Thêm dữ liệu mẫu trước mỗi test case
        AchievementEntity achievement1 = AchievementEntity.builder()
                .title("Achievement 1")
                .value(100)
                .description("First Achievement")
                .isActive(true)
                .build();

        AchievementEntity achievement2 = AchievementEntity.builder()
                .title("Achievement 2")
                .value(200)
                .description("Second Achievement")
                .isActive(false)
                .build();

        AchievementEntity achievement3 = AchievementEntity.builder()
                .title("Achievement 3")
                .value(300)
                .description("Third Achievement")
                .isActive(true)
                .build();

        achievementRepository.saveAll(List.of(achievement1, achievement2, achievement3));
    }

    @Test
    void testExistsByTitle_whenTitleExistsAndActive_thenReturnTrue() {
        // Act
        boolean exists = achievementRepository.exitstsByTitle("Achievement 1");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByTitle_whenTitleDoesNotExist_thenReturnFalse() {
        // Act
        boolean exists = achievementRepository.exitstsByTitle("Non-existent Achievement");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void testFindAllByIsActiveTrue_whenActiveRecordsExist_thenReturnRecords() {
        // Act
        List<AchievementEntity> activeAchievements = achievementRepository.findAllByIsActiveTrue();

        // Assert
        assertThat(activeAchievements).hasSize(2);
        assertThat(activeAchievements).extracting("title").contains("Achievement 1", "Achievement 3");
    }

    @Test
    void testFindAllByIsActiveTrue_whenNoActiveRecords_thenReturnEmptyList() {
        // Arrange
        achievementRepository.deleteAll();
        AchievementEntity inactiveAchievement = AchievementEntity.builder()
                .title("Inactive Achievement")
                .value(400)
                .description("No Active Records")
                .isActive(false)
                .build();
        achievementRepository.save(inactiveAchievement);

        // Act
        List<AchievementEntity> activeAchievements = achievementRepository.findAllByIsActiveTrue();

        // Assert
        assertThat(activeAchievements).isEmpty();
    }

    @Test
    void testSave_whenNewAchievement_thenSaveSuccessfully() {
        // Arrange
        AchievementEntity newAchievement = AchievementEntity.builder()
                .title("New Achievement")
                .value(500)
                .description("Newly added achievement")
                .isActive(true)
                .build();

        // Act
        AchievementEntity savedAchievement = achievementRepository.save(newAchievement);

        // Assert
        assertThat(savedAchievement.getId()).isNotNull();
        assertThat(savedAchievement.getTitle()).isEqualTo("New Achievement");
    }

    @Test
    void testFindById_whenIdExists_thenReturnAchievement() {
        // Arrange
        AchievementEntity existingAchievement = achievementRepository.findAll().get(0);
        Long existingId = existingAchievement.getId();

        // Act
        AchievementEntity foundAchievement = achievementRepository.findById(existingId).orElse(null);

        // Assert
        assertThat(foundAchievement).isNotNull();
        assertThat(foundAchievement.getId()).isEqualTo(existingId);
    }

    @Test
    void testFindById_whenIdDoesNotExist_thenReturnNull() {
        // Act
        AchievementEntity foundAchievement = achievementRepository.findById(999L).orElse(null);

        // Assert
        assertThat(foundAchievement).isNull();
    }

    @Test
    void testDeleteById_whenIdExists_thenDeleteSuccessfully() {
        // Arrange
        AchievementEntity existingAchievement = achievementRepository.findAll().get(0);
        Long existingId = existingAchievement.getId();

        // Act
        achievementRepository.deleteById(existingId);

        // Assert
        assertThat(achievementRepository.findById(existingId)).isEmpty();
    }

    @Test
    void testFindAll_whenMultipleRecordsExist_thenReturnAllRecords() {
        // Act
        List<AchievementEntity> allAchievements = achievementRepository.findAll();

        // Assert
        assertThat(allAchievements).hasSize(3);
    }
}
