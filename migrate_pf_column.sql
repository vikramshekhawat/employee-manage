-- Migration script to migrate from pf_percentage to pf_amount
-- This script will:
-- 1. Update pf_amount values from pf_percentage where pf_amount is 0 or NULL
-- 2. Drop the pf_percentage column
-- Run this in your MySQL database: mysql -u root -p emp_manage_db < migrate_pf_column.sql

USE emp_manage_db;

-- Step 1: Check current state (for verification)
SELECT 'Before migration - Current state:' AS info;
SELECT id, name, base_salary, pf_percentage, pf_amount FROM employees LIMIT 5;

-- Step 2: Update pf_amount from pf_percentage for rows where pf_amount is 0 or NULL
-- This preserves manually set values (like 700.00, 500.00) and only updates 0 values
-- Calculate: pf_amount = base_salary * (pf_percentage / 100)
UPDATE employees 
SET pf_amount = ROUND(base_salary * (pf_percentage / 100), 2)
WHERE (pf_amount = 0.00 OR pf_amount IS NULL) 
  AND pf_percentage IS NOT NULL 
  AND pf_percentage > 0;

-- Step 3: Show migration results
SELECT 'After data migration - Results:' AS info;
SELECT 
    id, 
    name, 
    base_salary, 
    pf_percentage, 
    pf_amount,
    CASE 
        WHEN pf_amount = ROUND(base_salary * (pf_percentage / 100), 2) THEN 'Migrated from percentage'
        WHEN pf_amount > 0 AND pf_amount != ROUND(base_salary * (pf_percentage / 100), 2) THEN 'Manual value preserved'
        ELSE 'Needs review'
    END AS status
FROM employees;

-- Step 4: Drop the old pf_percentage column
ALTER TABLE employees DROP COLUMN pf_percentage;

-- Step 5: Verify the change
SELECT 'Final state - Column structure:' AS info;
DESCRIBE employees;

-- Step 6: Verify data integrity
SELECT 'Final data verification:' AS info;
SELECT id, name, base_salary, pf_amount FROM employees;
