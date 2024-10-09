package com.pivinadanang.blog.services.post;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface IPostRedisService {
    //Clear cached data in Redis
    void clear();//clear cache
    List<PostResponse> getAllPosts(String keyword, Long categoryId, PostStatus status, YearMonth createdAt, PageRequest pageRequest) throws JsonProcessingException;
    void saveAllPosts(List<PostResponse> postResponses, String keyword, Long categoryId,PostStatus status, YearMonth createdAt, PageRequest pageRequest) throws JsonProcessingException;

    List<PostResponse> getRecentPosts(PageRequest pageRequest) throws JsonProcessingException;
    void saveRecentPosts(List<PostResponse> postResponses, PageRequest pageRequest) throws JsonProcessingException;

}
