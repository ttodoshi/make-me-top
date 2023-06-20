FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8102
ENTRYPOINT ["java","-jar","/app/planet-service-1.0-SNAPSHOT.jar"]
