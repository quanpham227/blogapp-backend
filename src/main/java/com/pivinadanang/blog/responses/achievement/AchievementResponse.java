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
    private String title;
    private Integer value;
    private String description;
    @JsonProperty("is_active")
    private Boolean isActive;


    public static AchievementResponse fromAchievement (AchievementEntity achievement) {
        AchievementResponse achievementResponse =  AchievementResponse.builder()
                .id(achievement.getId())
                .title(achievement.getTitle() != null ? achievement.getTitle() : "") // Xử lý null với giá trị mặc định rỗng
                .value(achievement.getValue() != null ? achievement.getValue() : 0) // Xử lý null với giá trị mặc định 0
                .description(achievement.getDescription() != null ? achievement.getDescription() : "") // Xử lý null với giá trị mặc định rỗng
                .isActive(achievement.getIsActive() != null ? achievement.getIsActive() : false) // Xử lý null với giá trị mặc định false
                .build();
        achievementResponse.setCreatedAt(achievement.getCreatedAt());
        achievementResponse.setUpdatedAt(achievement.getUpdatedAt());

        return achievementResponse;
    }
}
