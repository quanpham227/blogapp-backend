package com.pivinadanang.blog.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDTO {
    @NotEmpty(message = "Title is required")
    @Size(max = 100, message = "Title must be less than or equal to 100 characters")
    private String title;

    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value must be greater than or equal to 0")
    @Max(value = 2147483647, message = "Value must be less than or equal to 2147483647")
    private Integer value;

    @Size(max = 255, message = "Description must be less than or equal to 255 characters")
    private String description;

    @JsonProperty("is_active")
    private Boolean isActive;
}
