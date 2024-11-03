package com.pivinadanang.blog.responses.achievement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.AchievementEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementResponse  extends BaseResponse {
    private Long id;
    private String key;
    private Integer value;
    private String description;
    @JsonProperty("is_active")
    private Boolean isActive;


    public static AchievementResponse fromAchievement (AchievementEntity achievement) {
        AchievementResponse achievementResponse =  AchievementResponse.builder()
                .id(achievement.getId())
                .key(achievement.getKey())
                .value(achievement.getValue())
                .description(achievement.getDescription())
                .isActive(achievement.getIsActive())
                .build();
        achievementResponse.setCreatedAt(achievement.getCreatedAt());
        achievementResponse.setUpdatedAt(achievement.getUpdatedAt());

        return achievementResponse;
    }
}
