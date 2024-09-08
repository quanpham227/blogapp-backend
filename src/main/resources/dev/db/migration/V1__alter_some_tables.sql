-- versioned migrations
ALTER TABLE categories MODIFY name VARCHAR(50) UNIQUE;
ALTER TABLE categories MODIFY code VARCHAR(50) UNIQUE;


-- Thay đổi cột name trong bảng clients để đảm bảo là UNIQUE
ALTER TABLE clients MODIFY name VARCHAR(50) UNIQUE;

-- Đặt giá trị mặc định cho cột role_id trong bảng users
ALTER TABLE `users` ALTER COLUMN `role_id` SET DEFAULT 2;

-- đặt giá trị mặc định cho cột status trong bảng slides = true
ALTER TABLE slides MODIFY COLUMN status BOOLEAN NOT NULL DEFAULT TRUE;

