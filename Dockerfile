# Stage 1: Build the application
FROM maven:3.8.3-amazoncorretto-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml for dependency resolution
COPY pom.xml .

# Download dependencies (this layer will be cached)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM amazoncorretto:17-alpine3.19

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create volume for logs
VOLUME /logs

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port 8085
EXPOSE 8083

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

# Set entrypoint with configurable JVM options
ENTRYPOINT ["java", "-jar", "/app/app.jar"]