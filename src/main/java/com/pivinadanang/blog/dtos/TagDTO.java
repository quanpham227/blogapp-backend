package com.pivinadanang.blog.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDTO {
    @NotEmpty(message = "Tag name cannot be empty")
    private String name;
}
