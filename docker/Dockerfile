FROM gradle:8.9-jdk21 AS localbuilder
WORKDIR /builder
COPY build.gradle.kts settings.gradle.kts gradle.properties .
COPY gradle ./gradle
RUN gradle build --no-daemon || return 0
COPY . .
RUN gradle --no-daemon installDist

FROM amazoncorretto:21-alpine AS localapp
WORKDIR /app
COPY --from=localbuilder /builder/build/install/dennys/ ./
ENTRYPOINT ["./bin/dennys"]

FROM gradle:8.9-jdk21 AS cibuilder
WORKDIR /builder
COPY gradle ./gradle
RUN gradle build --no-daemon || return 0
COPY . .
RUN gradle --no-daemon installDist

FROM amazoncorretto:21-alpine AS ciapp
WORKDIR /app
COPY --from=cibuilder /builder/build/install/dennys/ ./
ENTRYPOINT ["./bin/dennys"]
