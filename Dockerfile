# Build stage
FROM gradle:8.6-jdk21 AS builder
WORKDIR /app
# Copy just the build files first to cache dependencies
COPY build.gradle.kts settings.gradle.kts ./
# Download dependencies
RUN gradle dependencies --no-daemon
# Now copy the source code
COPY src ./src
# Build the application
RUN gradle bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 