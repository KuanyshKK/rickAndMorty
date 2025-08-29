# syntax=docker/dockerfile:1

# ===== Build =====
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline
COPY . .
RUN mvn -B -DskipTests clean package

# ===== Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Spring возьмёт порт из $PORT (см. application.yml)
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
