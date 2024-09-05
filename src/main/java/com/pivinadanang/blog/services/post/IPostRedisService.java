package com.pivinadanang.blog.services.post;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.pivinadanang.blog.responses.post.PostResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IPostRedisService {
    //Clear cached data in Redis
    void clear();//clear cache
    List<PostResponse> getAllPosts(String keyword, Long categoryId, PageRequest pageRequest) throws JsonProcessingException;
    void saveAllPosts(List<PostResponse> postResponses, String keyword, Long categoryId, PageRequest pageRequest) throws JsonProcessingException;
}
