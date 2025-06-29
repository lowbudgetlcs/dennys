FROM gradle:8.9-jdk17 AS dependencies
WORKDIR /app
ENV GRADLE_USER_HOME=/cache
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN gradle --no-daemon clean build || true

FROM gradle:8.9-jdk17 AS builder
WORKDIR /builder
COPY --from=dependencies /cache /home/gradle/.gradle
COPY . .
RUN gradle --no-daemon installDist

FROM gradle:8.9-jdk17 AS test
WORKDIR /test
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN gradle test

FROM amazoncorretto:17-alpine AS app
WORKDIR /app
COPY --from=builder /builder/build/install/dennys/ ./
CMD ["./bin/dennys"]
