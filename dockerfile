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

# Copy source code and package application (Skipping tests)
COPY src ./src
RUN ./mvnw clean package -Dmaven.test.skip=true

# Stage 2: Runtime Environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

LABEL maintainer="harshthakur0729@gmail.com"

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the single built .jar file directly to app.jar
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]