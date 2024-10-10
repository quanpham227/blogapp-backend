-- Thêm cột comment_count vào bảng posts để lưu trữ số lượng bình luận
ALTER TABLE posts ADD COLUMN comment_count INT DEFAULT 0;