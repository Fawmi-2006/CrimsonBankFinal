-- CrimsonBank - Loan Data Verification Script
-- Run this script to verify that loan data was loaded correctly

USE crimsonbank;

-- Display header
SELECT '================================================' AS '';
SELECT 'CRIMSONBANK LOAN DATA VERIFICATION' AS '';
SELECT '================================================' AS '';
SELECT '' AS '';

-- 1. Total loan count
SELECT 'Total Loans in Database:' AS 'CHECK 1';
SELECT COUNT(*) AS total_loans FROM loans;
SELECT '' AS '';

-- 2. Loans by status
SELECT 'Loans by Status:' AS 'CHECK 2';
SELECT
    status,
    COUNT(*) AS count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM loans), 1), '%') AS percentage
FROM loans
GROUP BY status
ORDER BY count DESC;
SELECT '' AS '';

-- 3. Loan amount statistics
SELECT 'Loan Amount Statistics:' AS 'CHECK 3';
SELECT
    MIN(loan_amount) AS min_amount,
    MAX(loan_amount) AS max_amount,
    AVG(loan_amount) AS avg_amount,
    SUM(loan_amount) AS total_disbursed
FROM loans
WHERE status = 'APPROVED';
SELECT '' AS '';

-- 4. Loans by customer (top 5)
SELECT 'Top 5 Customers by Loan Count:' AS 'CHECK 4';
SELECT
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    COUNT(l.loan_id) AS loan_count,
    SUM(l.loan_amount) AS total_loan_amount
FROM customers c
JOIN loans l ON c.customer_id = l.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY loan_count DESC, total_loan_amount DESC
LIMIT 5;
SELECT '' AS '';

-- 5. Interest rate distribution
SELECT 'Interest Rate Distribution:' AS 'CHECK 5';
SELECT
    CASE
        WHEN interest_rate < 7.0 THEN '< 7.0%'
        WHEN interest_rate BETWEEN 7.0 AND 7.9 THEN '7.0% - 7.9%'
        WHEN interest_rate BETWEEN 8.0 AND 8.9 THEN '8.0% - 8.9%'
        WHEN interest_rate BETWEEN 9.0 AND 9.9 THEN '9.0% - 9.9%'
        ELSE '>= 10.0%'
    END AS rate_range,
    COUNT(*) AS loan_count
FROM loans
GROUP BY rate_range
ORDER BY MIN(interest_rate);
SELECT '' AS '';

-- 6. Tenure distribution
SELECT 'Loan Tenure Distribution:' AS 'CHECK 6';
SELECT
    CASE
        WHEN tenure_months <= 24 THEN '0-2 years'
        WHEN tenure_months <= 48 THEN '2-4 years'
        WHEN tenure_months <= 60 THEN '4-5 years'
        WHEN tenure_months <= 96 THEN '5-8 years'
        WHEN tenure_months <= 180 THEN '8-15 years'
        ELSE '> 15 years'
    END AS tenure_range,
    COUNT(*) AS loan_count,
    AVG(loan_amount) AS avg_amount
FROM loans
GROUP BY tenure_range
ORDER BY MIN(tenure_months);
SELECT '' AS '';

-- 7. Recent pending loans (for UI testing)
SELECT 'Recent Pending Loans (Top 10):' AS 'CHECK 7';
SELECT
    l.loan_id,
    l.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    l.loan_amount,
    l.interest_rate,
    l.tenure_months,
    l.status,
    DATE_FORMAT(l.created_at, '%Y-%m-%d %H:%i') AS created_at
FROM loans l
JOIN customers c ON l.customer_id = c.customer_id
WHERE l.status = 'PENDING'
ORDER BY l.created_at DESC
LIMIT 10;
SELECT '' AS '';

-- 8. Recently approved loans (for UI testing)
SELECT 'Recently Approved Loans (Top 10):' AS 'CHECK 8';
SELECT
    l.loan_id,
    l.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    l.loan_amount,
    l.interest_rate,
    l.tenure_months,
    l.status,
    DATE_FORMAT(l.approval_date, '%Y-%m-%d %H:%i') AS approval_date
FROM loans l
JOIN customers c ON l.customer_id = c.customer_id
WHERE l.status = 'APPROVED'
ORDER BY l.approval_date DESC
LIMIT 10;
SELECT '' AS '';

-- 9. Loan categories by amount (estimated)
SELECT 'Loan Categories (by Amount):' AS 'CHECK 9';
SELECT
    CASE
        WHEN loan_amount < 100000 THEN 'Personal Loan'
        WHEN loan_amount BETWEEN 100000 AND 500000 THEN 'Vehicle/Education Loan'
        WHEN loan_amount BETWEEN 500001 AND 1000000 THEN 'Business Loan'
        ELSE 'Home Loan'
    END AS loan_category,
    COUNT(*) AS count,
    AVG(interest_rate) AS avg_rate,
    AVG(tenure_months) AS avg_tenure_months
FROM loans
GROUP BY loan_category
ORDER BY MIN(loan_amount);
SELECT '' AS '';

-- 10. Final summary
SELECT '================================================' AS '';
SELECT 'VERIFICATION COMPLETE' AS '';
SELECT '================================================' AS '';
SELECT 'Expected Results:' AS '';
SELECT '- Total Loans: 43' AS '';
SELECT '- Approved: 15' AS '';
SELECT '- Pending: 23' AS '';
SELECT '- Rejected: 5' AS '';
SELECT '' AS '';
SELECT 'If numbers match, loan data is loaded correctly!' AS '';
SELECT '================================================' AS '';
