-- V5__Update_slides_and_clients_tables.sql

-- Xóa các cột không cần thiết từ bảng slides
ALTER TABLE slides
DROP COLUMN public_id,
DROP COLUMN image_url,
DROP COLUMN link;

-- Xóa các cột không cần thiết từ bảng clients
ALTER TABLE clients
DROP COLUMN public_id,
DROP COLUMN logo;

-- Thêm cột image_id vào bảng slides
ALTER TABLE slides
    ADD COLUMN image_id BIGINT;

-- Thêm cột image_id vào bảng clients
ALTER TABLE clients
    ADD COLUMN image_id BIGINT;

-- Thiết lập khóa ngoại cho cột image_id trong bảng slides
ALTER TABLE slides
    ADD CONSTRAINT fk_slides_image
        FOREIGN KEY (image_id) REFERENCES images(id);

-- Thiết lập khóa ngoại cho cột image_id trong bảng clients
ALTER TABLE clients
    ADD CONSTRAINT fk_clients_image
        FOREIGN KEY (image_id) REFERENCES images(id);