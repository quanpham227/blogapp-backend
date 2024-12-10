package com.pivinadanang.blog.responses.comment;
import com.pivinadanang.blog.responses.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CommentListResponse {
    private List<CommentResponse> comments;
    private int totalPages;
}
