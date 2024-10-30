-- Xóa bảng comments cũ
DROP TABLE IF EXISTS comments;

-- Tạo bảng comments mới với cột parent_comment_id và ràng buộc khóa ngoại
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    parent_comment_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- Thêm chỉ mục cho parent_comment_id để cải thiện hiệu suất truy vấn
CREATE INDEX idx_parent_comment_id ON comments(parent_comment_id);