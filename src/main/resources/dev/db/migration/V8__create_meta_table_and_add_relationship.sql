-- Tạo bảng meta
CREATE TABLE meta (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      meta_title VARCHAR(255) NULL,
                      meta_description TEXT NULL,
                      meta_keywords VARCHAR(255) NULL,
                      og_title VARCHAR(255) NULL,
                      og_description TEXT NULL,
                      og_image VARCHAR(255) NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Thêm cột meta_id vào bảng posts
ALTER TABLE posts
    ADD COLUMN meta_id BIGINT NULL;

-- Thiết lập khóa ngoại giữa bảng posts và meta
ALTER TABLE posts
    ADD CONSTRAINT fk_posts_meta
        FOREIGN KEY (meta_id)
            REFERENCES meta(id)
            ON DELETE CASCADE;

-- Index cột meta_id để tối ưu hóa truy vấn
CREATE INDEX idx_posts_meta_id ON posts(meta_id);
