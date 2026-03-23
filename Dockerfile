# === Build Stage ===
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy dependency files first for better layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x ./mvnw

# Download dependencies and plugins
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application, skipping tests
RUN ./mvnw package -DskipTests -B

# Extract layered JAR for optimized Docker layer caching
RUN java -Djarmode=layertools -jar target/*.jar extract --destination /extracted

# === Runtime Stage ===
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Install curl for the HEALTHCHECK
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser -d /app appuser

# Copy extracted layers in order of change frequency, ensuring correct ownership
COPY --chown=appuser:appuser --from=builder /extracted/dependencies/ ./
COPY --chown=appuser:appuser --from=builder /extracted/spring-boot-loader/ ./
COPY --chown=appuser:appuser --from=builder /extracted/snapshot-dependencies/ ./
COPY --chown=appuser:appuser --from=builder /extracted/application/ ./

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# JVM flags optimized for containers (removed legacy urandom flag)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+ExitOnOutOfMemoryError"

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start the application using 'exec' to allow graceful shutdown
ENTRYPOINT [ "sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher" ]
