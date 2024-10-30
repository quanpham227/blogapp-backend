package com.pivinadanang.blog.services.tag;


import com.pivinadanang.blog.responses.tag.TagResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITagService {
    List<TagResponse> getTopTags(Pageable pageable);
}
