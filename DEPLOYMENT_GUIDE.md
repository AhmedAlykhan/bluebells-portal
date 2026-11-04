# Local Deployment & Operations Guide — Bluebells School LMS

## 1. Prerequisites & Environment Verification

Before running the application, ensure the following tools are installed and operational:
- **Java**: OpenJDK 17.0.x LTS (tested with Homebrew OpenJDK 17)
- **Maven**: Apache Maven 3.9.x (or use the included `./mvnw`)
- **Node.js**: v20.x or v22.x LTS (tested with Node v22.23.3 and npm 10.9.9)
- **Database**: MySQL 8.0+ running on port 3306

---

## 2. Quick Start (Local Development)

### Step 1: Database Setup
Connect to local MySQL and create the application databases:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -e "CREATE DATABASE IF NOT EXISTS bluebells_school_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -h 127.0.0.1 -P 3306 -u root -e "CREATE DATABASE IF NOT EXISTS bluebells_school_test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### Step 2: Configure Environment Variables
Copy the sample environment file in `backend`:
```bash
cd backend
cp .env.example .env
```
Ensure database credentials match your local MySQL configuration:
```env
SPRING_PROFILES_ACTIVE=dev
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=bluebells_school_db
DB_USER=root
DB_PASSWORD=
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

### Step 3: Run Backend Migrations & Start Server
```bash
cd backend
mvn clean spring-boot:run
```
The Spring Boot server starts at `http://localhost:8080`.
- Swagger / OpenAPI UI: `http://localhost:8080/swagger-ui.html`
- Health Endpoint: `http://localhost:8080/actuator/health`

### Step 4: Start Frontend Development Server
In a separate terminal window:
```bash
cd frontend
npm install
npm run dev
```
The React development server opens at `http://localhost:5173`.

---

## 3. Production Build & Packaging

### Backend Artifact
```bash
cd backend
mvn clean package -DskipTests
# Produces runnable jar at target/bluebells-lms-0.0.1-SNAPSHOT.jar
java -jar target/bluebells-lms-0.0.1-SNAPSHOT.jar
```

### Frontend Production Bundle
```bash
cd frontend
npm run build
# Produces static distribution at frontend/dist/
```
