package com.pivinadanang.blog.services.comment;



import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.responses.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
    CommentEntity insertComment(CommentDTO comment);

    void deleteComment(Long commentId);
    void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;

    List<CommentResponse> getCommentsByUserAndPost(Long userId, Long productId);
    List<CommentResponse> getCommentsByPost(Long productId);
    void generateFakeComments() throws Exception;

    CommentResponse getCommentById(Long commentId);
}
