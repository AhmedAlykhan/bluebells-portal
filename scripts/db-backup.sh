#!/usr/bin/env bash
set -euo pipefail

# ==============================================================================
# Bluebells School LMS - Production Database Backup Script
# Institution: Bluebells Schooling System, Latifabad, Hyderabad, Sindh, Pakistan
# ==============================================================================

BACKUP_DIR="$(pwd)/backups"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
BACKUP_FILE="${BACKUP_DIR}/bluebells_backup_${TIMESTAMP}.sql"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_NAME="${DB_NAME:-bluebells_school_db}"

mkdir -p "${BACKUP_DIR}"

echo "[INFO] Starting database backup for '${DB_NAME}'..."
mysqldump -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" \
  --set-gtid-purged=OFF \
  --single-transaction \
  --quick \
  --routines \
  --triggers \
  "${DB_NAME}" > "${BACKUP_FILE}"

# Calculate SHA256 checksum for audit & integrity
shasum -a 256 "${BACKUP_FILE}" > "${BACKUP_FILE}.sha256"

BACKUP_SIZE="$(ls -lh "${BACKUP_FILE}" | awk '{print $5}')"
echo "[SUCCESS] Backup created successfully:"
echo "          Location: ${BACKUP_FILE}"
echo "          Size:     ${BACKUP_SIZE}"
echo "          Checksum: $(cat "${BACKUP_FILE}.sha256")"
