package com.pivinadanang.blog.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDTO {
    @NotEmpty(message = "Key is required")
    @Size(max = 100, message = "Key must be less than or equal to 100 characters")
    private String key;

    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value must be greater than or equal to 0")
    private Integer value;

    @Size(max = 255, message = "Description must be less than or equal to 255 characters")
    private String description;

    @JsonProperty("is_active")
    private Boolean isActive;
}
