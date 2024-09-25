-- V6__Revert_V5_Update_slides_and_clients_tables.sql

-- Kiểm tra và xóa ràng buộc khóa ngoại cho cột image_id trong bảng slides
SET @fk_name = (SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'slides' AND CONSTRAINT_NAME = 'fk_slides_image');
SET @sql = IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE slides DROP FOREIGN KEY ', @fk_name), 'SELECT "Foreign key fk_slides_image does not exist"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra và xóa ràng buộc khóa ngoại cho cột image_id trong bảng clients
SET @fk_name = (SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'clients' AND CONSTRAINT_NAME = 'fk_clients_image');
SET @sql = IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE clients DROP FOREIGN KEY ', @fk_name), 'SELECT "Foreign key fk_clients_image does not exist"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra và xóa cột image_id từ bảng slides
SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'slides' AND COLUMN_NAME = 'image_id');
SET @sql = IF(@col_name IS NOT NULL, 'ALTER TABLE slides DROP COLUMN image_id', 'SELECT "Column image_id does not exist in slides"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra và xóa cột image_id từ bảng clients
SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'clients' AND COLUMN_NAME = 'image_id');
SET @sql = IF(@col_name IS NOT NULL, 'ALTER TABLE clients DROP COLUMN image_id', 'SELECT "Column image_id does not exist in clients"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm lại các cột đã bị xóa từ bảng slides
SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'slides' AND COLUMN_NAME = 'public_id');
SET @sql = IF(@col_name IS NULL, 'ALTER TABLE slides ADD COLUMN public_id VARCHAR(255)', 'SELECT "Column public_id already exists in slides"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'slides' AND COLUMN_NAME = 'image_url');
SET @sql = IF(@col_name IS NULL, 'ALTER TABLE slides ADD COLUMN image_url VARCHAR(2048)', 'SELECT "Column image_url already exists in slides"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'slides' AND COLUMN_NAME = 'link');
SET @sql = IF(@col_name IS NULL, 'ALTER TABLE slides ADD COLUMN link VARCHAR(255)', 'SELECT "Column link already exists in slides"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm lại các cột đã bị xóa từ bảng clients
SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'clients' AND COLUMN_NAME = 'public_id');
SET @sql = IF(@col_name IS NULL, 'ALTER TABLE clients ADD COLUMN public_id VARCHAR(255)', 'SELECT "Column public_id already exists in clients"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_name = (SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'clients' AND COLUMN_NAME = 'logo');
SET @sql = IF(@col_name IS NULL, 'ALTER TABLE clients ADD COLUMN logo VARCHAR(2048)', 'SELECT "Column logo already exists in clients"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;