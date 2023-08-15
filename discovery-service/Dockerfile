FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/discovery-service-1.0-SNAPSHOT.jar"]
