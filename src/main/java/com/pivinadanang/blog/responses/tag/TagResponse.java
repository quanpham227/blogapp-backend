package com.pivinadanang.blog.responses.tag;

import com.pivinadanang.blog.models.TagEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagResponse {
    private Long id;
    private String name;
    private String slug;

    // Các phương thức getter và setter

    public static TagResponse fromTag(TagEntity tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setSlug(tag.getSlug());
        return response;
    }
}
