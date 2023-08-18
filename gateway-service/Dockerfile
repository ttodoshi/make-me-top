FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 4401
ENTRYPOINT ["java","-jar","/app/gateway-service-1.0-SNAPSHOT.jar"]
