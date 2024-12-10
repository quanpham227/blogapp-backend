package com.pivinadanang.blog.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    @JsonProperty("total_posts")
    private Long totalPosts;

    @JsonProperty("active_users")
    private Long activeUsers;

    @JsonProperty("comments_today")
    private Long commentsToday;

    @JsonProperty("recent_posts")
    private List<PostResponse> recentPosts;

    @JsonProperty("comments_per_day_last_week")
    private List<Long> commentsPerDayLastWeek;

    @JsonProperty("page_views_per_day_last_week")
    private List<Long> pageViewsPerDayLastWeek;
}
