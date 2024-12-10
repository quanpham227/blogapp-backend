package com.pivinadanang.blog.services.comment;



import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.dtos.UpdateCommentDTO;
import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {
    CommentResponse insertComment(CommentDTO comment);
    CommentResponse updateComment(Long id, UpdateCommentDTO updateCommentDTO) throws DataNotFoundException;
    CommentResponse replyComment(CommentDTO commentDTO);
    List<CommentResponse> getCommentsByUserAndPost(Long userId, Long productId);
    List<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable);
    void generateFakeComments() throws Exception;
    void deleteComment(Long commentId) throws DataNotFoundException;
    CommentResponse getCommentById(Long commentId) throws DataNotFoundException;
    Page<CommentResponse> findAll(String keyword, CommentStatus status,Pageable pageable) throws Exception;

    void updateCommentStatus(Long commentId, CommentStatus status) throws Exception;
}
