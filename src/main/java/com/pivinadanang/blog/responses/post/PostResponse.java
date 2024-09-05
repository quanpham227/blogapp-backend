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

    private String excerpt;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    // Thêm trường totalPages
    private int totalPages;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    private String status;



    public static PostResponse fromPost (PostEntity post){
        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .thumbnailUrl(post.getThumbnail())
                .status(post.getStatus().name())
                .categoryId(post.getCategory().getId())
                .categoryName(post.getCategory().getName())
                .build();
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());
        return postResponse;
    }

}
