# Backup & Disaster Recovery Guide — Bluebells School LMS

## 1. Relational Database Backup Strategy

To ensure zero loss of academic, student, and financial ledger data, backups should be taken using atomic, non-blocking table snapshots.

### Daily / On-Demand Backup Command
```bash
mysqldump -h 127.0.0.1 -P 3306 -u root \
  --single-transaction \
  --quick \
  --routines \
  --triggers \
  --default-character-set=utf8mb4 \
  bluebells_school_db > "bluebells_backup_$(date +%Y%m%d_%H%M%S).sql"
```

---

## 2. Restoration & Verification Procedure

**CRITICAL RULE**: Never restore backups over an active production database without testing the restoration file in an isolated verification database first.

### Step 1: Create Temporary Verification Database
```bash
mysql -h 127.0.0.1 -P 3306 -u root -e "DROP DATABASE IF EXISTS bluebells_verify_restore; CREATE DATABASE bluebells_verify_restore CHARACTER SET utf8mb4;"
```

### Step 2: Restore into Verification Database
```bash
mysql -h 127.0.0.1 -P 3306 -u root bluebells_verify_restore < bluebells_backup_YYYYMMDD_HHMMSS.sql
```

### Step 3: Run Data Integrity Checks
```bash
mysql -h 127.0.0.1 -P 3306 -u root -e "
  SELECT 'Users' AS Entity, COUNT(*) AS Count FROM bluebells_verify_restore.users
  UNION ALL
  SELECT 'Students', COUNT(*) FROM bluebells_verify_restore.students
  UNION ALL
  SELECT 'Invoices', COUNT(*) FROM bluebells_verify_restore.fee_invoices
  UNION ALL
  SELECT 'Payments', COUNT(*) FROM bluebells_verify_restore.payments;
"
```

---

## 3. Uploaded Document Storage Backup
Document uploads stored in `/var/bluebells/storage/` (or locally configured `./storage/`) are backed up via synchronized archive:
```bash
tar -czvf "bluebells_documents_$(date +%Y%m%d_%H%M%S).tar.gz" -C ./storage .
```
