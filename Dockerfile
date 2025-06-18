FROM gradle:8.9-jdk17 AS cache
WORKDIR /app
ENV GRADLE_USER_HOME=/cache
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN gradle --no-daemon clean build || true

FROM gradle:8.9-jdk17 AS builder
WORKDIR /builder
COPY --from=cache /cache /home/gradle/.gradle
COPY . .
RUN gradle --no-daemon installDist

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /builder/build/install/dennys/ ./

CMD ["./bin/dennys"]
