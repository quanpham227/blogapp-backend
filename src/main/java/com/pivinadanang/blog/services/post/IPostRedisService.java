package com.pivinadanang.blog.services.post;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface IPostRedisService {
    //Clear cached data in Redis
    void clear();//clear cache
    List<PostResponse> getAllPosts(String keyword, Long categoryId, PostStatus status, YearMonth createdAt, Pageable pageable) throws JsonProcessingException;
    void saveAllPosts(List<PostResponse> postResponses, String keyword, Long categoryId,PostStatus status, YearMonth createdAt, Pageable pageable) throws JsonProcessingException;

    List<PostResponse> getRecentPosts(Pageable pageable) throws JsonProcessingException;
    void saveRecentPosts(List<PostResponse> postResponses, Pageable pageable) throws JsonProcessingException;

}
