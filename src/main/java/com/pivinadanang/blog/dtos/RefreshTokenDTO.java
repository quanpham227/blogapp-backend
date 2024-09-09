package com.pivinadanang.blog.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenDTO {
    @NotBlank
    @JsonProperty("refresh_token")
    private String refreshToken;
}
