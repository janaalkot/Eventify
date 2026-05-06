FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY eventify-common/pom.xml eventify-common/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml

RUN mvn -q -pl notification-service -am dependency:go-offline

COPY eventify-common/src eventify-common/src
COPY notification-service/src notification-service/src

RUN mvn -q -pl notification-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
