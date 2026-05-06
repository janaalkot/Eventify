FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY eventify-common/pom.xml eventify-common/pom.xml
COPY event-service/pom.xml event-service/pom.xml

RUN mvn -q -pl event-service -am dependency:go-offline

COPY eventify-common/src eventify-common/src
COPY event-service/src event-service/src

RUN mvn -q -pl event-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/event-service/target/event-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
