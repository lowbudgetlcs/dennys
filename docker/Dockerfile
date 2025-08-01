FROM gradle:8.9-jdk21 AS builder
WORKDIR /builder
COPY build.gradle.kts settings.gradle.kts gradle.properties .
COPY gradle ./gradle
RUN gradle build --no-daemon || return 0
COPY . .
RUN gradle --no-daemon installDist

FROM amazoncorretto:21-alpine AS local_app
WORKDIR /app
COPY --from=builder /builder/build/install/dennys/ ./
ENTRYPOINT ["./bin/dennys"]
