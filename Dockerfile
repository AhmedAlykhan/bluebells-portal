# Stage 1: Build JAR using Maven and Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml backend/settings.xml ./
COPY backend/src ./src
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
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

# Healthcheck targeting Actuator health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://127.0.0.1:${PORT:-8080}/actuator/health || exit 1

# Execute Java with shell variable expansion and exec signal propagation
ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]
