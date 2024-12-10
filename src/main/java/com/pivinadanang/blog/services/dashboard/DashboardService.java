package com.pivinadanang.blog.services.dashboard;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.CommentRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.responses.dashboard.DashboardResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class DashboardService implements IDashboardService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public DashboardResponse getDashboardData() {
        Pageable pageable = PageRequest.of(0, 3);
        // Lấy top 3 bài viết không có status là DELETED
        List<PostResponse> topPosts = postRepository.findTop3PostsExcludingStatus(PostStatus.DELETED, pageable)
                .stream()
                .map(PostResponse::fromPost)
                .collect(Collectors.toList());
        // Lấy tổng số bài viết
        Long totalPosts = postRepository.count();
        // Lấy số lượng người dùng hoạt động
        Long activeUsers = userRepository.countActiveUsers();

        // Lấy số lượng bình luận trong ngày
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Long commentsToday = commentRepository.countTodayComments(startOfDay, endOfDay);
        // Lấy khoảng thời gian 7 ngày qua, bao gồm cả hôm nay
        Map<String, LocalDateTime> timeFrame = getLast7DaysTimeFrame();
        LocalDateTime startDate = timeFrame.get("startDate");
        LocalDateTime endDate = timeFrame.get("endDate");

        // Lấy dữ liệu bình luận trong 7 ngày qua
        List<Object[]> rawData = commentRepository.countCommentsPerDayLastWeek(startDate, endDate);

        // Tạo danh sách 7 ngày qua (định dạng dd/MM)
        List<String> last7Days = IntStream.range(0, 7)
                .mapToObj(i -> LocalDate.now().minusDays(6 - i).format(DateTimeFormatter.ofPattern("dd/MM")))
                .toList();

        // Chuyển dữ liệu thô thành Map, với key là ngày (dd/MM), value là số lượng bình luận
        Map<String, Long> commentsMap = rawData.stream()
                .collect(Collectors.toMap(
                        r -> ((java.sql.Date) r[0]).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM")), // Chuyển ngày từ query thành chuỗi dd/MM
                        r -> ((Number) r[1]).longValue() // Chuyển số lượng bình luận thành Long (nếu cần)
                ));

        // Tạo danh sách kết quả đầy đủ với giá trị mặc định là 0 nếu ngày không có dữ liệu
        List<Long> commentsPerDayLastWeek = last7Days.stream()
                .map(day -> commentsMap.getOrDefault(day, 0L)) // Kiểm tra ngày trong map, nếu không có trả về 0
                .toList();

        // Tổng page views trong 7 ngày qua
        List<Long> pageViewsPerDayLastWeek = postRepository.countPageViewsPerDayLastWeek();

        return new DashboardResponse(totalPosts, activeUsers, commentsToday, topPosts, commentsPerDayLastWeek, pageViewsPerDayLastWeek);

    }

    // Utility method to get start and end date for the last 7 days (including today)
    private Map<String, LocalDateTime> getLast7DaysTimeFrame() {
        LocalDate today = LocalDate.now();
        LocalDateTime endDate = today.atStartOfDay().plusDays(1).minusNanos(1); // Cuối ngày hôm nay
        LocalDateTime startDate = endDate.minusDays(7); // 7 ngày trước (bao gồm hôm nay)

        Map<String, LocalDateTime> timeFrame = new HashMap<>();
        timeFrame.put("startDate", startDate);
        timeFrame.put("endDate", endDate);
        return timeFrame;
    }
}
