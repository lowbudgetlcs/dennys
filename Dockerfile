FROM node:22-alpine AS frontend
WORKDIR /client
COPY /src/main/resources/client/package*.json .
RUN npm install
COPY /src/main/resources/client .
RUN npm run build

FROM gradle:8.9-jdk17 AS build
WORKDIR /app
COPY . .
COPY --from=frontend /client/build /app/src/main/resources/client
RUN gradle shadowJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar

CMD ["java", "-jar", "app.jar"]
