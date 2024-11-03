package com.pivinadanang.blog.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDTO {
    @NotEmpty(message = "Tag name cannot be empty")
    @Size(max = 100, message = "Tag name must be less than or equal to 100 characters")
    private String name;
}
