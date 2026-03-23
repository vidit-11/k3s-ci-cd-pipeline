# === Build Stage ===
# Using the official Maven image so we don't need local wrapper files
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application, skipping tests
RUN mvn package -DskipTests -B

# Extract layered JAR for optimized Docker layer caching
RUN java -Djarmode=layertools -jar target/*.jar extract --destination /extracted

# === Runtime Stage ===
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Install curl for the HEALTHCHECK
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser -d /app appuser

# Copy extracted layers in order of change frequency
COPY --chown=appuser:appuser --from=builder /extracted/dependencies/ ./
COPY --chown=appuser:appuser --from=builder /extracted/spring-boot-loader/ ./
COPY --chown=appuser:appuser --from=builder /extracted/snapshot-dependencies/ ./
COPY --chown=appuser:appuser --from=builder /extracted/application/ ./

# Switch to non-root user
USER appuser

EXPOSE 8080

# JVM flags optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT [ "sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher" ]
