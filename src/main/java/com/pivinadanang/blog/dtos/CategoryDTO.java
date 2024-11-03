package com.pivinadanang.blog.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category cannot be empty")
    @Size(min = 3, max = 100, message = "Category name must be between 3 and 50 characters")
    private String name;
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
