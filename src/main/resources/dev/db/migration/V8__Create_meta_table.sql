-- V8__Create_meta_table.sql
CREATE TABLE meta (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,         -- Khóa chính cho bảng meta
                      post_id BIGINT NOT NULL,                      -- Khóa ngoại liên kết với bảng post
                      meta_title VARCHAR(255),                      -- Thẻ meta title (SEO tiêu đề)
                      meta_description TEXT,                        -- Thẻ meta description (SEO mô tả ngắn gọn)
                      meta_keywords VARCHAR(255),                   -- Thẻ meta keywords (từ khóa SEO)
                      og_title VARCHAR(255),                        -- Open Graph title (tiêu đề cho mạng xã hội)
                      og_description TEXT,                          -- Open Graph description (mô tả cho mạng xã hội)
                      og_image VARCHAR(255),                        -- Open Graph image (hình ảnh chia sẻ qua mạng xã hội)
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Ngày tạo
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Ngày cập nhật
                      CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE, -- Khóa ngoại với bảng post
                      UNIQUE (post_id)                              -- Đảm bảo mỗi bài viết chỉ có một bộ thông tin meta
);