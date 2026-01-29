CREATE DATABASE IF NOT EXISTS crimsonbank;
USE crimsonbank;

CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    staff_id INT,
    customer_id INT,
    account_id INT,
    transaction_id INT,
    loan_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE SET NULL,
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE SET NULL,
    INDEX idx_action (action),
    INDEX idx_created_at (created_at),
    INDEX idx_staff_id (staff_id)
);
