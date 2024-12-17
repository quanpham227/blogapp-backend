package com.pivinadanang.blog.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ImageListResponse {
    private List<ImageResponse> images;
    private int totalPages;
    private HttpStatus status;
    private Long totalFileSizes;

    public void setComments(List<ImageResponse> images) {
        this.images = images != null ? images : new ArrayList<>();  // Đảm bảo không có null
    }
}
