FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8104
ENTRYPOINT ["java","-jar","/app/explorer-service-1.0-SNAPSHOT.jar"]
