FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8105
ENTRYPOINT ["java","-jar","/app/course-registration-service-1.0-SNAPSHOT.jar"]
