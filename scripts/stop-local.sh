#!/usr/bin/env bash

echo "Stopping Bluebells School LMS local servers..."

if [ -f backend.pid ]; then
  PID=$(cat backend.pid)
  echo "Stopping backend (PID: $PID)..."
  kill "$PID" 2>/dev/null || true
  rm -f backend.pid
fi

if [ -f frontend.pid ]; then
  PID=$(cat frontend.pid)
  echo "Stopping frontend (PID: $PID)..."
  kill "$PID" 2>/dev/null || true
  rm -f frontend.pid
fi

# Clean up any lingering port listeners
lsof -ti :5173 | xargs kill -9 2>/dev/null || true
lsof -ti :8080 | xargs kill -9 2>/dev/null || true

echo "All services stopped."
