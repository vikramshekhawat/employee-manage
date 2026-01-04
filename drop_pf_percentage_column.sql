-- Query to drop pf_percentage column from employees table
-- Run this if the column still exists after migration

USE emp_manage_db;

-- Drop the pf_percentage column
ALTER TABLE employees DROP COLUMN pf_percentage;

-- Verify the column has been removed
DESCRIBE employees;

