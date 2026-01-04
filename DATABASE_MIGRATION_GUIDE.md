# Database Migration Guide: PF Column Migration

## 🔴 Current Issue
The database has **both** `pf_percentage` and `pf_amount` columns. We need to:
1. Migrate data from `pf_percentage` to `pf_amount` (for rows where `pf_amount` is 0)
2. Preserve manually set `pf_amount` values (like 700.00, 500.00)
3. Drop the old `pf_percentage` column

---

## ✅ Solution: Run the Migration Script

### **Step 1: Backup Your Database (Recommended)**

```bash
mysqldump -u root -p emp_manage_db > backup_before_pf_migration.sql
```

### **Step 2: Run the Migration Script**

**Option A: Using MySQL Command Line**
```bash
mysql -u root -p emp_manage_db < migrate_pf_column.sql
```

**Option B: Using MySQL Client**
1. Connect to MySQL:
   ```bash
   mysql -u root -p
   ```

2. Select the database:
   ```sql
   USE emp_manage_db;
   ```

3. Run the migration commands:
   ```sql
   -- Update pf_amount from pf_percentage for rows where pf_amount is 0
   UPDATE employees 
   SET pf_amount = ROUND(base_salary * (pf_percentage / 100), 2)
   WHERE (pf_amount = 0.00 OR pf_amount IS NULL) 
     AND pf_percentage IS NOT NULL 
     AND pf_percentage > 0;
   
   -- Drop the old column
   ALTER TABLE employees DROP COLUMN pf_percentage;
   ```

### **Step 3: Verify the Migration**

Check that the column was dropped and data is correct:
```sql
DESCRIBE employees;
SELECT id, name, base_salary, pf_amount FROM employees;
```

You should see:
- ✅ Only `pf_amount` column exists (no `pf_percentage`)
- ✅ All employees have `pf_amount` values (not 0, unless manually set to 0)

### **Step 4: Restart the Backend**

After the migration, restart your Spring Boot application:
```bash
.\gradlew.bat bootRun
```

---

## 📊 What the Migration Does

1. **Preserves Manual Values**: If an employee has `pf_amount = 700.00` or `500.00` (manually set), it keeps that value
2. **Migrates from Percentage**: For employees with `pf_amount = 0.00`, it calculates: `pf_amount = base_salary × (pf_percentage / 100)`
3. **Drops Old Column**: Removes `pf_percentage` column after data migration

---

## 🔍 Example Migration

**Before:**
- Employee ID 2: `base_salary = 50000`, `pf_percentage = 12.00`, `pf_amount = 0.00`
- Employee ID 7: `base_salary = 60000`, `pf_percentage = 12.00`, `pf_amount = 700.00` (manual)

**After:**
- Employee ID 2: `base_salary = 50000`, `pf_amount = 6000.00` (calculated: 50000 × 12%)
- Employee ID 7: `base_salary = 60000`, `pf_amount = 700.00` (preserved manual value)

---

## ⚠️ Important Notes

1. **Data Preservation**: This migration preserves all existing `pf_amount` values that are not 0
2. **Calculation**: For rows with `pf_amount = 0`, it calculates from `pf_percentage`
3. **No Data Loss**: All data is migrated before dropping the old column
4. **Backup First**: Always backup your database before running migrations

---

## 🐛 Troubleshooting

### Issue: "Column 'pf_percentage' doesn't exist"
- **Solution**: The column was already dropped. Check with `DESCRIBE employees;`

### Issue: "Duplicate column name 'pf_amount'"
- **Solution**: The column already exists. The migration will just update the data.

### Issue: Some employees still have `pf_amount = 0`
- **Solution**: Check if they had `pf_percentage = 0` or NULL. You may need to manually set these values.

---

## ✅ Verification Checklist

After migration:
- [ ] `pf_percentage` column is removed
- [ ] `pf_amount` column exists with correct data type
- [ ] All employees have `pf_amount` values (except those manually set to 0)
- [ ] Backend application starts without errors
- [ ] Can add new employees successfully
- [ ] Can view/edit existing employees

---

**Status**: Ready for migration  
**Date**: January 2026
