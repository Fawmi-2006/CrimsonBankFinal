SELECT staff_id, username, password FROM staff
WHERE password NOT LIKE '$2%$%$%'
LIMIT 5;

ALTER TABLE staff ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;
ALTER TABLE customers ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;