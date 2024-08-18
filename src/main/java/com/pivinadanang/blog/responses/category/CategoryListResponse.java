package com.pivinadanang.blog.responses.category;

import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CategoryListResponse {
    private List<CategoryResponse> categoies;
    private int totalPages;
}
