-- Thêm các thuộc tính vào bảng posts
ALTER TABLE posts
    ADD COLUMN visibility VARCHAR(255) NOT NULL,
ADD COLUMN revision_count INT DEFAULT 0 NOT NULL,
ADD COLUMN view_count INT DEFAULT 0 NOT NULL;

-- Tạo bảng tags
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng trung gian post_tags để thiết lập quan hệ nhiều-nhiều giữa posts và tags
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           PRIMARY KEY (post_id, tag_id),
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Thêm chỉ số cho các cột thường xuyên được truy vấn
CREATE INDEX idx_post_visibility ON posts(visibility);
CREATE INDEX idx_post_revision_count ON posts(revision_count);
CREATE INDEX idx_post_view_count ON posts(view_count);