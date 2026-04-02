# Stage 1: Build the application
# Using Maven 3.9+ which has better support for Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
# Copy only pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight runtime image
# Using the supported Eclipse Temurin JRE image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Correct path: builder stage WORKDIR was /app, so JAR is in /app/target/
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
