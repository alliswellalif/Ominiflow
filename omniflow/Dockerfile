# ===================================================================
# OmniFlow Enterprise - Multi-Stage Production Dockerfile
# ===================================================================

# Stage 1: Build JAR with Maven & OpenJDK
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Distroless JRE Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as non-privileged system user for enterprise container security
RUN groupadd -r omniflow && useradd -r -g omniflow omniflow
USER omniflow

COPY --from=builder /app/target/omniflow-platform-1.0.0.jar app.jar

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=${PORT}", "-jar", "app.jar"]

