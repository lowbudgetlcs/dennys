FROM gradle:8.9-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon || true
COPY src ./src
RUN gradle shadowJar --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar

CMD ["java", "-jar", "app.jar"]

