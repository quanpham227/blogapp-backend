package com.pivinadanang.blog.responses.category;

import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse  {
    private Long id;

    private String name;

    private String code;

    private String description;

    private Long postCount;


    public static CategoryResponse fromCategory(CategoryEntity category, Long postCount) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName() != null ? category.getName() : "")
                .code(category.getCode() != null ? category.getCode() : "")
                .description(category.getDescription() != null ? category.getDescription() : "")
                .postCount(postCount)
                .build();
    }
    // Phương thức không có postCount
    public static CategoryResponse fromCategory(CategoryEntity category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName() != null ? category.getName() : "")
                .code(category.getCode() != null ? category.getCode() : "")
                .description(category.getDescription() != null ? category.getDescription() : "")
                .postCount(0L) // Mặc định postCount là 0 nếu không có thông tin
                .build();
    }
}
