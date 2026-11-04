#!/usr/bin/env bash
set -euo pipefail

echo "========================================================"
echo " Bluebells School LMS — Full CI-Style Local Verification"
echo "========================================================"

export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
echo "[1/3] Running Backend Compilation, Flyway Migrations & Integration Tests..."
mvn clean test -s backend/settings.xml -f backend/pom.xml

echo "[2/3] Running Frontend Unit & Component Tests..."
npm --prefix frontend run test

echo "[3/3] Running Frontend Typecheck & Production Build..."
npm --prefix frontend run build

echo "========================================================"
echo " ALL LOCAL QUALITY GATES VERIFIED SUCCESSFULLY"
echo "========================================================"
