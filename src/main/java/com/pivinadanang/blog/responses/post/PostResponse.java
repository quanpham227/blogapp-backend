package com.pivinadanang.blog.responses.post;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import com.pivinadanang.blog.responses.MetaResponse;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.responses.user.UserResponse;
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

    private CategoryResponse category;

    private String status;

    @JsonProperty("author_name")
   private String authorName;

   @JsonProperty("profile_image")
   private String profileImage;

    private MetaResponse meta;

    @JsonProperty("favorite_count")
    private Long favoriteCount;

    @JsonProperty("comment_count")
    private Long commentCount;

    public static PostResponse fromPost (PostEntity post){
        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .thumbnailUrl(post.getThumbnail())
                .status(post.getStatus().name())
                .category(CategoryResponse.fromCategory(post.getCategory()))
                .authorName(post.getUser().getFullName())
                .profileImage(post.getUser().getProfileImage())
                .meta(MetaResponse.fromMeta(post.getMeta()))
                .build();
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());
        return postResponse;
    }

}
