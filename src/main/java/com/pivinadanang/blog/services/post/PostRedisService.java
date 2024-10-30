package com.pivinadanang.blog.services.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PostRedisService implements IPostRedisService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    @Value("${cache.prefix.posts}")
    private String postCachePrefix;

    @Value("${cache.prefix.recent_posts}")
    private String recentPostCachePrefix;

    private String getKeyFrom(String keyword, Long categoryId, PostStatus status, YearMonth createdAt, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        Sort sort = pageable.getSort();
        String sortDirection = sort.getOrderFor("createdAt")
                .getDirection() == Sort.Direction.ASC ? "asc": "desc";
        String createdAtStr = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM")) : "null";

        String key = String.format("%s:%s:%d:%s:%s:%d:%d:%s",postCachePrefix,
                keyword,
                categoryId,
                status != null ? status.name() : "null",
                createdAtStr,
                pageNumber,
                pageSize,
                sortDirection);
        return key;
        /*
        {
            "all_products:1:10:asc": "list of products object"
        }
        * */
    }
    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    public List<PostResponse> getAllPosts(String keyword, Long categoryId, PostStatus status, YearMonth createdAt, Pageable pageable) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, status, createdAt, pageable);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<PostResponse> postResponses =
                json != null ?
                        redisObjectMapper.readValue(json, new TypeReference<List<PostResponse>>() {})
                        : null;
        return postResponses;
    }

    @Override
    public void saveAllPosts(List<PostResponse> postResponses, String keyword, Long categoryId,PostStatus status, YearMonth createdAt, Pageable pageable) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, status, createdAt, pageable);
        String json = redisObjectMapper.writeValueAsString(postResponses);
        redisTemplate.opsForValue().set(key, json);
    }

    @Override
    public List<PostResponse> getRecentPosts(Pageable pageable) throws JsonProcessingException {
        String key = String.format("%s:recent:%d:%d", recentPostCachePrefix, pageable.getPageNumber(), pageable.getPageSize());
        String json = (String) redisTemplate.opsForValue().get(key);
        List<PostResponse> postResponses =
                json != null ?
                        redisObjectMapper.readValue(json, new TypeReference<List<PostResponse>>() {})
                        : null;
        return postResponses;
    }

    @Override
    public void saveRecentPosts(List<PostResponse> postResponses, Pageable pageable) throws JsonProcessingException {
        String key = String.format("%s:recent:%d:%d", recentPostCachePrefix, pageable.getPageNumber(), pageable.getPageSize());
        String json = redisObjectMapper.writeValueAsString(postResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
