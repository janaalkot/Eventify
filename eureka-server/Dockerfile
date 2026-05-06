FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml

RUN mvn -q -pl eureka-server -am dependency:go-offline

COPY eureka-server/src eureka-server/src

RUN mvn -q -pl eureka-server -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
