package com.pivinadanang.blog.responses.post;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse extends BaseResponse {
    private Long id;

    private String title;

    private String content;

    private String slug;

    private String thumbnailUrl;

    @JsonProperty("category_id")
    private Long categoryId;

    public static PostResponse fromPost (PostEntity post){
        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .slug(post.getSlug())
                .categoryId(post.getCategory().getId())
                .thumbnailUrl(post.getImage().getImageUrl())
                .build();
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());
        return postResponse;
    }

}