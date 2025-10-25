# =========================================================
#  STAGE 1 — Build with Maven (with dependency caching)
# =========================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Pre-download dependencies (this layer will be cached)
RUN ./mvnw dependency:go-offline -B

# Now copy source code and build the JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# =========================================================
#  STAGE 2 — Runtime image (small, lightweight)
# =========================================================
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose service port
EXPOSE 8080

# Start Spring Boot app
ENTRYPOINT ["java", "-jar", "/app.jar"]

