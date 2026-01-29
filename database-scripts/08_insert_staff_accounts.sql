CREATE DATABASE IF NOT EXISTS crimsonbank;
USE crimsonbank;

INSERT INTO staff (email, username, password, full_name, role, is_active) VALUES
('admin@crimsonbank.com', 'admin', 'admin123', 'Administrator', 'ADMIN', TRUE),
('supervisor@crimsonbank.com', 'supervisor', 'supervisor123', 'Supervisor', 'SUPERVISOR', TRUE),
('staff@crimsonbank.com', 'staff', 'staff123', 'Staff Member', 'STAFF', TRUE),
('manager@crimsonbank.com', 'manager', 'manager123', 'Branch Manager', 'MANAGER', TRUE),
('teller@crimsonbank.com', 'teller', 'teller123', 'Bank Teller', 'TELLER', TRUE),
('accountant@crimsonbank.com', 'accountant', 'accountant123', 'Senior Accountant', 'ACCOUNTANT', TRUE),
('compliance@crimsonbank.com', 'compliance', 'compliance123', 'Compliance Officer', 'COMPLIANCE_OFFICER', TRUE);

INSERT INTO customers (first_name, last_name, nic, email, phone_number, address, city, postal_code, date_of_birth) VALUES
('Rajesh', 'Kumar', '123456789V', 'rajesh@example.com', '+94701234567', '123 Main Street', 'Colombo', '00100', '1990-05-15'),
('Priya', 'Silva', '234567890V', 'priya@example.com', '+94702345678', '456 Park Avenue', 'Kandy', '20000', '1992-08-22'),
('Rohan', 'Perera', '345678901V', 'rohan@example.com', '+94703456789', '789 Oak Lane', 'Galle', '80000', '1988-03-10'),
('Anjali', 'Sharma', '456789012V', 'anjali@example.com', '+94704567890', '321 Elm Street', 'Colombo', '00200', '1995-11-25'),
('Arjun', 'Nair', '567890123V', 'arjun@example.com', '+94705678901', '654 Maple Drive', 'Negombo', '11500', '1991-07-18'),
('Divya', 'Patel', '678901234V', 'divya@example.com', '+94706789012', '987 Pine Road', 'Colombo', '00300', '1993-09-30'),
('Vikram', 'Gupta', '789012345V', 'vikram@example.com', '+94707890123', '147 Cedar Lane', 'Kandy', '20001', '1989-02-14'),
('Sneha', 'Desai', '890123456V', 'sneha@example.com', '+94708901234', '258 Birch Street', 'Galle', '80001', '1994-06-08'),
('Sanjay', 'Iyer', '901234567V', 'sanjay@example.com', '+94709012345', '369 Spruce Avenue', 'Colombo', '00400', '1987-01-12'),
('Neha', 'Kapoor', '012345678V', 'neha@example.com', '+94710123456', '741 Walnut Way', 'Kandy', '20002', '1996-04-20');

INSERT INTO accounts (customer_id, account_number, account_type, balance, status) VALUES
(1, 'ACC001001', 'SAVINGS', 50000.00, 'ACTIVE'),
(1, 'ACC001002', 'CURRENT', 100000.00, 'ACTIVE'),
(2, 'ACC002001', 'SAVINGS', 75000.00, 'ACTIVE'),
(2, 'ACC002002', 'CURRENT', 150000.00, 'ACTIVE'),
(3, 'ACC003001', 'SAVINGS', 45000.00, 'ACTIVE'),
(3, 'ACC003002', 'CURRENT', 120000.00, 'ACTIVE'),
(4, 'ACC004001', 'SAVINGS', 60000.00, 'ACTIVE'),
(4, 'ACC004002', 'CURRENT', 180000.00, 'ACTIVE'),
(5, 'ACC005001', 'SAVINGS', 55000.00, 'ACTIVE'),
(5, 'ACC005002', 'CURRENT', 110000.00, 'ACTIVE'),
(6, 'ACC006001', 'SAVINGS', 70000.00, 'ACTIVE'),
(6, 'ACC006002', 'CURRENT', 160000.00, 'ACTIVE'),
(7, 'ACC007001', 'SAVINGS', 48000.00, 'ACTIVE'),
(7, 'ACC007002', 'CURRENT', 130000.00, 'ACTIVE'),
(8, 'ACC008001', 'SAVINGS', 65000.00, 'ACTIVE'),
(8, 'ACC008002', 'CURRENT', 170000.00, 'ACTIVE'),
(9, 'ACC009001', 'SAVINGS', 52000.00, 'ACTIVE'),
(9, 'ACC009002', 'CURRENT', 140000.00, 'ACTIVE'),
(10, 'ACC010001', 'SAVINGS', 80000.00, 'ACTIVE'),
(10, 'ACC010002', 'CURRENT', 200000.00, 'ACTIVE');
