FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8108
ENTRYPOINT ["java","-jar","/app/admin-service-1.0-SNAPSHOT.jar"]
