FROM eclipse-temurin:17-jdk AS build
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew ./
RUN ./gradlew clean build --no-daemon
COPY . .
RUN ./gradlew installDist --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /build/install/dennys ./

CMD ["./bin/dennys"]
