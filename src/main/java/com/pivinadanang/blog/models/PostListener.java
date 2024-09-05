package com.pivinadanang.blog.models;


import com.pivinadanang.blog.services.post.IPostRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class PostListener {
    private final IPostRedisService postRedisService;
    private static final Logger logger = LoggerFactory.getLogger(PostListener.class);
    @PrePersist
    public void prePersist(PostEntity post) {
        logger.info("prePersist");
    }

    @PostPersist //save = persis
    public void postPersist(PostEntity post) {
        // Update Redis cache
        logger.info("postPersist");
        postRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(PostEntity post) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(PostEntity post) {
        // Update Redis cache
        logger.info("postUpdate");
        postRedisService.clear();
    }

    @PreRemove
    public void preRemove(PostEntity post) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preRemove");
    }

    @PostRemove
    public void postRemove(PostEntity post) {
        // Update Redis cache
        logger.info("postRemove");
        postRedisService.clear();
    }
}
