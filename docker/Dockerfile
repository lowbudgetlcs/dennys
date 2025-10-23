FROM gradle:8.9-jdk21 AS builder 
WORKDIR /builder
COPY build.gradle.kts settings.gradle.kts gradle.properties .
COPY gradle ./gradle
RUN gradle build --no-daemon || return 0
COPY . .
RUN gradle --no-daemon installDist

FROM amazoncorretto:21-alpine AS local
WORKDIR /app
COPY --from=builder /builder/build/install/dennys/ ./
ENTRYPOINT ["./bin/dennys"]

FROM eclipse-temurin:21-jre-alpine AS deploy
WORKDIR /app
RUN ls -lrt
RUN pwd
ARG DIST_PATH 
RUN echo ${DIST_PATH}
COPY ${DIST_PATH}/ ./
ENTRYPOINT ["./bin/dennys"]
