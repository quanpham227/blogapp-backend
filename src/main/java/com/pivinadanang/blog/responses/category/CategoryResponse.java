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



    public static CategoryResponse fromCategory (CategoryEntity category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName() != null ? category.getName() : "") // Xử lý null với giá trị mặc định rỗng
                .code(category.getCode() != null ? category.getCode() : "") // Xử lý null với giá trị mặc định rỗng
                .description(category.getDescription() != null ? category.getDescription() : "") // Xử lý null với giá trị mặc định rỗng
                .build();
    }
}
