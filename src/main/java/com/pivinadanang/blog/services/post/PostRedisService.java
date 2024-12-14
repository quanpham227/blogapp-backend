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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostRedisService implements IPostRedisService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    @Value("${cache.prefix.posts}")
    private String postCachePrefix;

    @Value("${cache.prefix.recent_posts}")
    private String recentPostCachePrefix;


    @Value("${cache.prefix.post_by_slug}")
    private String postBySlugCachePrefix;

    private String getKeyFrom(String keyword, String categorySlug, String tagSlug, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        Sort sort = pageable.getSort();
        String sortDirection = sort.isSorted() ? Objects.requireNonNull(sort.getOrderFor("createdAt")).getDirection().name().toLowerCase() : "unsorted";

        String key = String.format("%s:%s:%s:%s:%d:%d:%s",
                postCachePrefix,
                keyword,
                categorySlug,
                tagSlug,
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
    public List<PostResponse> getRecentPosts(Pageable pageable) throws JsonProcessingException {
        try {
            String key = String.format("%s:recent:%d:%d", recentPostCachePrefix, pageable.getPageNumber(), pageable.getPageSize());
            String json = (String) redisTemplate.opsForValue().get(key);
            return json != null ? redisObjectMapper.readValue(json, new TypeReference<List<PostResponse>>() {}) : null;
        } catch (Exception e) {
            // Log lỗi và trả về null nếu có lỗi xảy ra
            // loggingService.logError("Error when getting recent posts from Redis", e);
            return null;
        }
    }
    @Override
    public void saveRecentPosts(List<PostResponse> postResponses, Pageable pageable) throws JsonProcessingException {
        try {
            String key = String.format("%s:recent:%d:%d", recentPostCachePrefix, pageable.getPageNumber(), pageable.getPageSize());
            String json = redisObjectMapper.writeValueAsString(postResponses);
            redisTemplate.opsForValue().set(key, json); // Thời gian sống của cache là 10 phút
        } catch (Exception e) {
            // Log lỗi nếu có lỗi xảy ra
            // loggingService.logError("Error when saving recent posts to Redis", e);
        }
    }

    @Override
    public List<PostResponse> getAllPosts(String keyword, String categorySlug, String tagSlug, Pageable pageable) throws JsonProcessingException {
        try {
            String key = this.getKeyFrom(keyword, categorySlug, tagSlug, pageable);
            String json = (String) redisTemplate.opsForValue().get(key);
            List<PostResponse> postResponses =
                    json != null ?
                            redisObjectMapper.readValue(json, new TypeReference<List<PostResponse>>() {})
                            : null;
            return postResponses;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveAllPosts(List<PostResponse> postResponses, String keyword, String categorySlug, String tagSlug, Pageable pageable) throws JsonProcessingException {
       try {
           String key = this.getKeyFrom(keyword, categorySlug, tagSlug, pageable);
           String json = redisObjectMapper.writeValueAsString(postResponses);
           redisTemplate.opsForValue().set(key, json);
         } catch (Exception e) {

       }
    }

    @Override
    public PostResponse getPostBySlug(String slug) throws JsonProcessingException {
        String key = getKeyFromSlug(slug);
        String json = (String) redisTemplate.opsForValue().get(key);
        return json != null ? redisObjectMapper.readValue(json, PostResponse.class) : null;
    }

    @Override
    public void savePostBySlug(PostResponse postResponse, String slug) throws JsonProcessingException {
        String key = getKeyFromSlug(slug);
        String json = redisObjectMapper.writeValueAsString(postResponse);
        redisTemplate.opsForValue().set(key, json);
    }
    private String getKeyFromSlug(String slug) {
        return String.format("%s:%s", postBySlugCachePrefix, slug);
    }
}
