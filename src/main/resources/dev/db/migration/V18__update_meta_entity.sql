-- V1__update_meta_entity.sql

ALTER TABLE meta
DROP COLUMN meta_keywords;

ALTER TABLE meta
    ADD COLUMN viewport VARCHAR(255) DEFAULT 'width=device-width, initial-scale=1.0',
ADD COLUMN robots VARCHAR(255) DEFAULT 'index, follow',
ADD COLUMN slug VARCHAR(255) UNIQUE;