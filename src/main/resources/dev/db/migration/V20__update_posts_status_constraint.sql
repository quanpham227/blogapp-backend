-- V20__update_posts_status_constraint.sql

-- Drop the existing constraint
ALTER TABLE posts DROP CONSTRAINT posts_chk_1;

-- Add the new constraint to allow status values 0, 1, 2, and 3
ALTER TABLE posts ADD CONSTRAINT posts_chk_1 CHECK (status IN (0, 1, 2, 3));