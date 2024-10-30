package com.pivinadanang.blog.services.tag;


import com.pivinadanang.blog.models.TagEntity;
import com.pivinadanang.blog.repositories.TagRepository;
import com.pivinadanang.blog.responses.tag.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService {
    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getTopTags(Pageable pageable) {
        List<TagEntity> tags = tagRepository.findTopTags(pageable);
        return tags.stream().map(TagResponse::fromTag).collect(Collectors.toList());
    }
}
