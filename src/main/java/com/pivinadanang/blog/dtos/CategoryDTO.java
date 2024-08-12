package com.pivinadanang.blog.dtos;

import com.pivinadanang.blog.ultils.SlugUtil;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category cannot be empty")
    private String name;
}
