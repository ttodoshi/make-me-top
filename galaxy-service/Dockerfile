FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8101
ENTRYPOINT ["java","-jar","/app/galaxy-service-1.0-SNAPSHOT.jar"]
