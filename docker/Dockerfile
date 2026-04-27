FROM gradle:8.13-jdk21 AS builder 
WORKDIR /builder
COPY build.gradle.kts settings.gradle.kts gradle.properties .
COPY gradle ./gradle
RUN gradle build --no-daemon || return 0
COPY . .
RUN gradle --no-daemon installDist

FROM amazoncorretto:21-alpine AS local
WORKDIR /app
RUN apk upgrade --no-cache && apk add argon2-dev
COPY --from=builder /builder/build/install/dennys/ ./
ENTRYPOINT ["./bin/dennys"]

FROM amazoncorretto:21-alpine AS deploy
WORKDIR /app
RUN apk upgrade --no-cache && apk add argon2-dev
ARG DIST_PATH 
COPY --chmod=755 ${DIST_PATH}/ ./
ENTRYPOINT ["./bin/dennys"]
