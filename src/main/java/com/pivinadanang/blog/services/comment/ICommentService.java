package com.pivinadanang.blog.services.comment;



import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.responses.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
    CommentResponse insertComment(CommentDTO comment);
    CommentResponse updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;
    CommentResponse replyComment(CommentDTO commentDTO);
    List<CommentResponse> getCommentsByUserAndPost(Long userId, Long productId);
    List<CommentResponse> getCommentsByPostId(Long postId);
    void generateFakeComments() throws Exception;
    void deleteComment(Long commentId) throws DataNotFoundException;
    CommentResponse getCommentById(Long commentId) throws DataNotFoundException;
}
