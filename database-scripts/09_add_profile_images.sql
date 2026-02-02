-- Add profile_image column to staff table
ALTER TABLE staff ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;

-- Add profile_image column to customers table
ALTER TABLE customers ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;
