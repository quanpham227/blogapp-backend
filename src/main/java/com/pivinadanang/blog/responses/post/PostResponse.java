package com.pivinadanang.blog.responses.post;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import com.pivinadanang.blog.responses.MetaResponse;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.responses.tag.TagResponse;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @JsonProperty("public_id")
    private String publicId;

    // Thêm trường totalPages
    private int totalPages;

    private CategoryResponse category;

    private String status;

    @JsonProperty("author_name")
   private String authorName;

   @JsonProperty("profile_image")
   private String profileImage;

   private String email;

    private MetaResponse meta;

    @JsonProperty("ratings_count")
    private int ratingsCount;

    @JsonProperty("comment_count")
    private int commentCount;

    @JsonProperty("view_count")
    private int viewCount;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("revision_count")
    private int revisionCount;

    private int priority;

    private List<TagResponse> tags;


    public static PostResponse fromPost(PostEntity post) {
        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .thumbnailUrl(post.getThumbnail())
                .publicId(post.getPublicId())
                .status(post.getStatus() != null ? post.getStatus().name() : "UNKNOWN")
                .category(post.getCategory() != null ? CategoryResponse.fromCategory(post.getCategory()) : null)
                .authorName(post.getUser() != null ? post.getUser().getFullName() : "Unknown Author")
                .profileImage(post.getUser() != null ? post.getUser().getProfileImage() : null)
                .email(post.getUser() != null ? post.getUser().getEmail() : null)
                .meta(post.getMeta() != null ? MetaResponse.fromMeta(post.getMeta()) : null)
                .commentCount(post.getCommentCount())
                .ratingsCount(post.getRatingsCount() )
                .viewCount(post.getViewCount() )
                .visibility(post.getVisibility() != null ? post.getVisibility().name() : "UNKNOWN")
                .revisionCount(post.getRevisionCount() )
                .priority(post.getPriority() )
                .tags(post.getTags() != null
                        ? post.getTags().stream().map(TagResponse::fromTag).collect(Collectors.toList())
                        : Collections.emptyList())
                .build();

        // Set thêm các trường ngày giờ
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());

        return postResponse;
    }


}
