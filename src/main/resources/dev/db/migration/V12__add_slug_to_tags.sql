-- V12__add_slug_to_tags.sql

ALTER TABLE tags
    ADD COLUMN slug VARCHAR(100) NOT NULL DEFAULT 'temp-slug';