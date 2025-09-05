FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR authentication-api/app

COPY authentication-api/pom.xml ./

COPY authentication-api/src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21

COPY --from=build authentication-api/app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]