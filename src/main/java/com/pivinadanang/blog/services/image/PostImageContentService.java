package com.pivinadanang.blog.services.image;

import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.PostImageContent;
import com.pivinadanang.blog.repositories.PostImageContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageContentService implements IPostImageContentService {
    private final PostImageContentRepository postImageContentRepository;
    @Override
    @Transactional
    public PostImageContent createPostImageContent(PostImageContent postImageContent) {
        return postImageContentRepository.save(postImageContent);
    }
}
