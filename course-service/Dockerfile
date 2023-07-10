FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8106
ENTRYPOINT ["java","-jar","/app/course-service-1.0-SNAPSHOT.jar"]
