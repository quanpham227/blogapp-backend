-- Migration Flyway MySQL: Update 'users' table for new changes
-- Ensure migration is safe
START TRANSACTION;

-- 1. Update email length to 255 and set default
ALTER TABLE users
    MODIFY COLUMN email VARCHAR(255)  NULL ;

-- 2. Update phone_number column to allow NULL
ALTER TABLE users
    MODIFY COLUMN phone_number VARCHAR(15) NULL;

-- 3. Update password column to allow NULL
ALTER TABLE users
    MODIFY COLUMN password VARCHAR(200) NULL;

-- Commit changes
COMMIT;
