FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8103
ENTRYPOINT ["java","-jar","/app/auth-service-1.0-SNAPSHOT.jar"]
