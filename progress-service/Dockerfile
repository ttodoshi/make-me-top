FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8109
ENTRYPOINT ["java","-jar","/app/progress-service-1.0-SNAPSHOT.jar"]
