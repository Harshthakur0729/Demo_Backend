# Stage 1: Build Java Application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy Maven wrapper and dependencies setup
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Ensure executable permissions on Maven wrapper script
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime Environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

LABEL maintainer="harshthakur0729@gmail.com"

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar file (skipping plain.jar if generated)
COPY --from=builder /build/target/*[!plain].jar app.jar

EXPOSE 8080

# Run Spring Boot with container-aware memory management
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]