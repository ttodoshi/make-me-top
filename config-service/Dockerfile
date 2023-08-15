FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app/config-service-1.0-SNAPSHOT.jar"]
