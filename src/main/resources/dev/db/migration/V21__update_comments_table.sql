-- V21__update_comments_table.sql

-- Thêm cột parent_comment_id
ALTER TABLE `comments`
    ADD COLUMN `parent_comment_id` BIGINT DEFAULT NULL;

-- Thêm cột status
ALTER TABLE `comments`
    ADD COLUMN `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Thêm ràng buộc khóa ngoại cho parent_comment_id
ALTER TABLE `comments`
    ADD CONSTRAINT `FK_parent_comment` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`id`);

-- Cập nhật cột content để khớp với định nghĩa trong entity
ALTER TABLE `comments`
    MODIFY COLUMN `content` TEXT CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL;