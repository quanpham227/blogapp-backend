-- Tạo bảng ratings
CREATE TABLE ratings (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         rating DECIMAL(3, 2) NOT NULL,
                         post_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Thiết lập khóa ngoại với bảng posts
                         CONSTRAINT fk_ratings_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    -- Thiết lập khóa ngoại với bảng users
                         CONSTRAINT fk_ratings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Thêm cột ratings_count vào bảng posts để lưu trữ số lượng đánh giá
ALTER TABLE posts ADD COLUMN ratings_count INT DEFAULT 0;