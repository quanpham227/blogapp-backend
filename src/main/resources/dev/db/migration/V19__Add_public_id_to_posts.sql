-- V19__Add_public_id_to_posts.sql

-- Bước 1: Thêm cột public_id với giá trị mặc định
ALTER TABLE posts
    ADD COLUMN public_id VARCHAR(255) NOT NULL DEFAULT 'default_public_id';

-- Bước 2: Cập nhật giá trị public_id cho các hàng hiện tại
UPDATE posts SET public_id = CONCAT('public_id_', id);

-- Bước 3: Loại bỏ giá trị mặc định
ALTER TABLE posts ALTER COLUMN public_id DROP DEFAULT;

-- Bước 4: Thêm ràng buộc UNIQUE
ALTER TABLE posts ADD CONSTRAINT unique_public_id UNIQUE (public_id);