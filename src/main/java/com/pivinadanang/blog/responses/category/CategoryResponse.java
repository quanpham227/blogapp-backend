package com.pivinadanang.blog.responses.category;

import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import lombok.*;

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

    private long postCount;

    public static CategoryResponse fromCategory (CategoryEntity category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .build();
    }
}
