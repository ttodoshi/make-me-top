FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8110
ENTRYPOINT ["java","-jar","/app/feedback-service-1.0-SNAPSHOT.jar"]
