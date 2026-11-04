#!/usr/bin/env bash
set -euo pipefail

echo "========================================================"
echo " Starting Bluebells School LMS (Local Stack)"
echo "========================================================"

if [ -d "/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home" ]; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
elif [ -d "/opt/homebrew/Cellar/openjdk@17/17.0.20.1/libexec/openjdk.jdk/Contents/Home" ]; then
  export JAVA_HOME="/opt/homebrew/Cellar/openjdk@17/17.0.20.1/libexec/openjdk.jdk/Contents/Home"
fi

echo "Checking MySQL connection on 127.0.0.1:3306..."
mysql -h 127.0.0.1 -P 3306 -u root -e "SHOW DATABASES LIKE 'bluebells%';" || {
  echo "Error: MySQL is not running on 127.0.0.1:3306"
  exit 1
}

echo "Starting Backend Spring Boot Application (Port 8080)..."
JAR_PATH="backend/target/bluebells-lms-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_PATH" ] || [ $(find backend/src -newer "$JAR_PATH" 2>/dev/null | wc -l) -gt 0 ]; then
  echo "Building backend JAR (new sources detected)..."
  mvn package -DskipTests -o -s backend/settings.xml -f backend/pom.xml
fi
nohup "$JAVA_HOME/bin/java" -jar "$JAR_PATH" > backend.log 2>&1 &
echo $! > backend.pid
echo "Backend running with PID $(cat backend.pid). Logs: backend.log"

echo "Starting Frontend Development Server (Port 5173)..."
nohup npm --prefix frontend run dev > frontend.log 2>&1 &
echo $! > frontend.pid
echo "Frontend running with PID $(cat frontend.pid). Logs: frontend.log"

echo "========================================================"
echo " Bluebells LMS is running!"
echo " Web UI:     http://localhost:5173"
echo " REST API:   http://localhost:8080/api/v1"
echo " OpenAPI UI: http://localhost:8080/swagger-ui.html"
echo " Health:     http://localhost:8080/actuator/health"
echo "========================================================"
