-- Migration script to update existing staff passwords with BCrypt hashes
-- IMPORTANT: This script should only be run ONCE to migrate plain-text passwords to BCrypt
-- For existing plain-text passwords, you'll need to use Java to hash them

-- First, let's check if there are any plain-text passwords (not BCrypt format)
-- BCrypt hashes start with $2a$, $2b$, or $2y$
SELECT staff_id, username, password FROM staff
WHERE password NOT LIKE '$2%$%$%'
LIMIT 5;

-- To properly migrate, follow these steps:
-- 1. Run the Java migration utility (create a new utility class if needed)
-- 2. Or update passwords one by one through the application's signup/update features
-- 3. All NEW passwords created through the app after deploying the update will be automatically hashed

-- Add profile_image column if not exists (this was done in 09_add_profile_images.sql)
ALTER TABLE staff ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;
ALTER TABLE customers ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;

-- Example: If you want to set a sample password, use this Java code outside the database:
-- String password = "admin123";
-- String hashedPassword = PasswordUtil.hashPassword(password);
-- Then update: UPDATE staff SET password = '[hashedPasswordResult]' WHERE username = 'admin';
