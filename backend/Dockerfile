# Stage 1: Build JAR using Maven and Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml settings.xml ./
COPY src ./src
RUN mvn clean package -DskipTests -s settings.xml

# Stage 2: Hardened Runtime with Temurin JRE 17 Alpine
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="Ahmed Ali Khan <medlykhan@gmail.com>"
LABEL project="Bluebells Schooling System"

# Install curl/wget for healthcheck
RUN apk add --no-cache curl wget bash

# Create non-root system group and user
RUN addgroup -S bluebells && adduser -S bluebells -G bluebells

# Set working directory and configure persistent storage paths
WORKDIR /app
RUN mkdir -p /var/bluebells/storage /var/bluebells/logs \
    && chown -R bluebells:bluebells /app /var/bluebells

# Copy artifact from build stage
COPY --from=build --chown=bluebells:bluebells /app/target/bluebells-lms-0.0.1-SNAPSHOT.jar app.jar

# Switch to non-root user
USER bluebells

# Default environment configuration
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATASOURCE_URL="jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/bluebells_school_db?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Karachi" \
    SPRING_DATASOURCE_USERNAME="2F3Ja9P3r4bNoZp.root" \
    SPRING_DATASOURCE_PASSWORD="ShebVLbjn7wyU4eK" \
    JWT_SECRET="jfcX15zDXK5Dy9f3IyLn5VVckpzXQhYzW/cSPKBqQeNtBAKceWCG1py4nU3pXp5XhNKfOX4NADzRxSoEwHhipw==" \
    CORS_ALLOWED_ORIGINS="*" \
    INITIAL_ADMIN_USERNAME="admin" \
    INITIAL_ADMIN_PASSWORD="Admin@12345" \
    JAVA_TOOL_OPTIONS="-Xms128m -Xmx260m -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -XX:CompressedClassSpaceSize=32m -XX:ReservedCodeCacheSize=32m"

EXPOSE 8080


# Execute Java with shell variable expansion and exec signal propagation
ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]
