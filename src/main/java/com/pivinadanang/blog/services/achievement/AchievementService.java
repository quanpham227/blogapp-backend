package com.pivinadanang.blog.services.achievement;

import com.pivinadanang.blog.dtos.AchievementDTO;
import com.pivinadanang.blog.models.AchievementEntity;
import com.pivinadanang.blog.repositories.AchievementRepository;
import com.pivinadanang.blog.responses.achievement.AchievementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService implements IAchievementService{
    private final AchievementRepository achievementRepository;

    @Override
    public List<AchievementResponse> getAllAchievementsForAdmin() {
        return achievementRepository.findAll().stream()
                .map(AchievementResponse::fromAchievement).toList();
    }

    @Override
    public List<AchievementResponse> getAllAchievementsForUser() {
        return achievementRepository.findAllByIsActiveTrue().stream()
                .map(AchievementResponse::fromAchievement).toList();
    }

    @Override
    public AchievementResponse addAchievement(@Valid AchievementDTO achievement) throws Exception {
        if (achievementRepository.exitstsByTitle(achievement.getTitle())) {
            throw new IllegalStateException("Title must be unique");
        }
        AchievementEntity newAchievement = AchievementEntity.builder()
                .title(achievement.getTitle())
                .value(achievement.getValue())
                .description(achievement.getDescription())
                .isActive(achievement.getIsActive() != null ? achievement.getIsActive() : true)
                .build();
        AchievementEntity savedAchievement = achievementRepository.save(newAchievement);
        return AchievementResponse.fromAchievement(savedAchievement);
    }

    @Override
    public AchievementResponse updateAchievement(Long id, AchievementDTO achievement) throws Exception {
        AchievementEntity achievementEntity = achievementRepository.findById(id)
                .orElseThrow(() -> new Exception("Achievement not found"));
        if (achievement.getTitle() != null && !achievement.getTitle().isEmpty()) {
           if (!achievement.getTitle().equals(achievementEntity.getTitle()) ) {
                if (achievementRepository.exitstsByTitle(achievement.getTitle())) {
                    throw new IllegalStateException("Achievement already exists");
                }
                achievementEntity.setTitle(achievement.getTitle());
           }
        }
        if (achievement.getValue() != null && achievement.getValue() >= 0) {
            achievementEntity.setValue(achievement.getValue());
        }
        if (achievement.getDescription() != null) {
            achievementEntity.setDescription(achievement.getDescription());
        }
        if (achievement.getIsActive() != null) {
            achievementEntity.setIsActive(achievement.getIsActive());
        }
        AchievementEntity updatedAchievement = achievementRepository.save(achievementEntity);
        return AchievementResponse.fromAchievement(updatedAchievement);
    }

    @Override
    public void deleteAchievement(Long id) throws Exception {
        AchievementResponse achievement = getAchievementById(id);
        achievementRepository.deleteById(achievement.getId());
    }

    @Override
    public AchievementResponse getAchievementById(Long id) throws Exception {
        return achievementRepository.findById(id)
                .map(AchievementResponse::fromAchievement)
                .orElseThrow(() -> new Exception("Achievement not found"));
    }

    @Override
    public boolean existsAchievementByTitle(String title) {
        return achievementRepository.exitstsByTitle(title);
    }
}
