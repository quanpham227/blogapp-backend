package com.pivinadanang.blog.services.achievement;


import com.pivinadanang.blog.dtos.AchievementDTO;
import com.pivinadanang.blog.responses.achievement.AchievementResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface IAchievementService {
    List<AchievementResponse> getAllAchievementsForAdmin();
    List<AchievementResponse> getAllAchievementsForUser();

    AchievementResponse addAchievement(@Valid AchievementDTO achievement) throws Exception;
    AchievementResponse updateAchievement(Long id, AchievementDTO achievement) throws Exception;
    void deleteAchievement(Long id) throws Exception;
    AchievementResponse getAchievementById(Long id) throws Exception;
    boolean existsAchievementByTitle(String title);
}
