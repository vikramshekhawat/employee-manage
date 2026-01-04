-- Fix migration script for PF column
-- This handles the case where both pf_percentage and pf_amount columns exist
-- Run this in your MySQL database

USE emp_manage_db;

-- Step 1: Check current state
SELECT 'Current columns:' AS info;
SHOW COLUMNS FROM employees LIKE 'pf%';

-- Step 2: Update pf_amount from pf_percentage for rows where pf_amount is 0 or NULL
-- This preserves manually set values (like 700.00, 500.00) and only updates 0 values
UPDATE employees 
SET pf_amount = ROUND(base_salary * (pf_percentage / 100), 2)
WHERE (pf_amount = 0.00 OR pf_amount IS NULL) 
  AND pf_percentage IS NOT NULL 
  AND pf_percentage > 0;

-- Step 3: Show migration results
SELECT 'Migration results:' AS info;
SELECT 
    id, 
    name, 
    base_salary, 
    pf_percentage, 
    pf_amount,
    CASE 
        WHEN pf_amount = ROUND(base_salary * (pf_percentage / 100), 2) THEN 'Migrated'
        WHEN pf_amount > 0 THEN 'Manual value preserved'
        ELSE 'Needs review'
    END AS status
FROM employees;

-- Step 4: Drop the old pf_percentage column (uncomment when ready)
-- ALTER TABLE employees DROP COLUMN pf_percentage;

-- Step 5: Verify final state
-- DESCRIBE employees;


