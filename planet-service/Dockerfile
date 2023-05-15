FROM maven:3.6.3-openjdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml compile
RUN mvn -f /home/app/pom.xml package -DskipTests
RUN mvn -f /home/app/pom.xml clean package -DskipTests

#
# Package stage
#
FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build /home/app/target/planet-service-1.0-SNAPSHOT.jar /usr/local/lib/planet-service.jar
EXPOSE 8102
ENTRYPOINT ["java","-jar","/usr/local/lib/planet-service.jar"]
