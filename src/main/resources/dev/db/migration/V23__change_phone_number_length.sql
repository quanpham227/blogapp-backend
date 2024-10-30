-- V23__change_phone_number_length.sql

-- Thay đổi độ dài của cột phone_number từ varchar(20) thành varchar(100)
ALTER TABLE `about`
    MODIFY COLUMN `phone_number` varchar(100) DEFAULT NULL;