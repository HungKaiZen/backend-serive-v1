#FROM openjdk:17
#
#ARG JAR_FILE=target/*.jar
#
#COPY ${JAR_FILE} backend-service.jar
#
#ENTRYPOINT ["java", "-jar", "backend-service.jar"]
#
#EXPOSE 8080




# Stage 1: Build với Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Chạy app với OpenJDK
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/backend-service.jar backend-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backend-service.jar"]
