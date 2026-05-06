FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY eventify-common/pom.xml eventify-common/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml

RUN mvn -q -pl api-gateway -am dependency:go-offline

COPY eventify-common/src eventify-common/src
COPY api-gateway/src api-gateway/src

RUN mvn -q -pl api-gateway -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
