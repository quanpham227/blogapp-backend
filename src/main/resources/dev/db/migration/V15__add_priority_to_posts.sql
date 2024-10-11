-- Thêm cột priority vào bảng posts
ALTER TABLE posts ADD COLUMN priority INT DEFAULT 0;

-- Khởi tạo giá trị priority bằng 0 cho tất cả các bản ghi hiện có
UPDATE posts SET priority = 0;

-- Đảm bảo cột priority không có giá trị NULL
ALTER TABLE posts MODIFY COLUMN priority INT NOT NULL;