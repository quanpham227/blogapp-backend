-- V14__remove_default_from_slug.sql

ALTER TABLE tags
    ALTER COLUMN slug DROP DEFAULT;