#!/usr/bin/env bash
set -euo pipefail

# ==============================================================================
# Bluebells School LMS - Backup Restore & Verification Drill Script
# Institution: Bluebells Schooling System, Latifabad, Hyderabad, Sindh, Pakistan
# ==============================================================================

BACKUP_DIR="$(pwd)/backups"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
SOURCE_DB="${DB_NAME:-bluebells_school_db}"
RESTORE_DB="bluebells_verify_restore"

LATEST_BACKUP="$(ls -t "${BACKUP_DIR}"/bluebells_backup_*.sql 2>/dev/null | head -n 1 || true)"
if [[ -z "${LATEST_BACKUP}" ]]; then
  echo "[ERROR] No backup file found in ${BACKUP_DIR}. Run ./scripts/db-backup.sh first."
  exit 1
fi

echo "[INFO] Verifying checksum for ${LATEST_BACKUP}..."
if [[ -f "${LATEST_BACKUP}.sha256" ]]; then
  shasum -a 256 -c "${LATEST_BACKUP}.sha256"
  echo "[INFO] Checksum verified."
fi

echo "[INFO] Preparing isolated verification schema: '${RESTORE_DB}'..."
mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -e "
  DROP DATABASE IF EXISTS ${RESTORE_DB};
  CREATE DATABASE ${RESTORE_DB} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
"

echo "[INFO] Restoring dump into '${RESTORE_DB}'..."
mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" "${RESTORE_DB}" < "${LATEST_BACKUP}"

echo "[INFO] Validating restored table count..."
SRC_TABLE_COUNT=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -N -e "
  SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${SOURCE_DB}';
")
RESTORE_TABLE_COUNT=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -N -e "
  SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${RESTORE_DB}';
")

echo "Source tables:    ${SRC_TABLE_COUNT}"
echo "Restored tables:  ${RESTORE_TABLE_COUNT}"

if [[ "${SRC_TABLE_COUNT}" -ne "${RESTORE_TABLE_COUNT}" ]]; then
  echo "[FAILURE] Table count mismatch between source and restored database!"
  exit 1
fi

echo "[INFO] Validating key table row counts..."
for TABLE in users roles permissions academic_sessions classes sections subjects students; do
  SRC_ROWS=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -N -e "SELECT COUNT(*) FROM ${SOURCE_DB}.${TABLE};")
  RESTORE_ROWS=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -N -e "SELECT COUNT(*) FROM ${RESTORE_DB}.${TABLE};")
  echo "  - Table '${TABLE}': Source=${SRC_ROWS}, Restored=${RESTORE_ROWS}"
  if [[ "${SRC_ROWS}" -ne "${RESTORE_ROWS}" ]]; then
    echo "[FAILURE] Row count mismatch on table ${TABLE}!"
    exit 1
  fi
done

echo "[INFO] Cleaning up verification schema '${RESTORE_DB}'..."
mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -e "DROP DATABASE IF EXISTS ${RESTORE_DB};"

echo "========================================================"
echo " DATABASE BACKUP & RESTORE DRILL VERIFIED SUCCESSFULLY"
echo "========================================================"
