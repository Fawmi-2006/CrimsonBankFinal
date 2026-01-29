CREATE DATABASE IF NOT EXISTS crimsonbank;
USE crimsonbank;

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255),
    related_account_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    FOREIGN KEY (related_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    INDEX idx_account_id (account_id),
    INDEX idx_created_at (created_at),
    INDEX idx_transaction_type (transaction_type)
);
