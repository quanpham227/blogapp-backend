package com.pivinadanang.blog.responses.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PostListResponse {
    private List<PostResponse> posts;
    private int totalPages;
}
