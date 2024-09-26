-- Tạo bảng about
CREATE TABLE about (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID của trang About
                       unique_key VARCHAR(255) NOT NULL UNIQUE, -- Khóa duy nhất để xác định bản ghi About
                       title VARCHAR(255) NOT NULL, -- Tiêu đề của trang About
                       content TEXT, -- Nội dung của trang About
                       image_url VARCHAR(2048), -- URL của hình ảnh đại diện cho trang About
                       address VARCHAR(255), -- Địa chỉ của công ty
                       phone_number VARCHAR(20), -- Số điện thoại của công ty
                       email VARCHAR(100), -- Email liên hệ của công ty
                       working_hours VARCHAR(255), -- Giờ làm việc của công ty
                       facebook_link VARCHAR(255), -- Liên kết đến trang Facebook của công ty
                       youtube VARCHAR(255), -- Liên kết đến trang YouTube của công ty
                       vision_statement TEXT, -- Tuyên bố tầm nhìn của công ty
                       founding_date VARCHAR(255), -- Ngày thành lập công ty
                       ceo_name VARCHAR(100) -- Tên của CEO hoặc người đứng đầu công ty
);