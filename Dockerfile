FROM gradle:8.9-jdk17 AS build
WORKDIR /build
COPY . .
RUN gradle installDist --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /build/build/install/dennys ./

CMD ["./bin/dennys"]
