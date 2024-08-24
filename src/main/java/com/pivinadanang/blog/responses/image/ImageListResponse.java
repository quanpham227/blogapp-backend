package com.pivinadanang.blog.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ImageListResponse {
    private List<ImageResponse> images;
    private int totalPages;
    private HttpStatus status;
}
