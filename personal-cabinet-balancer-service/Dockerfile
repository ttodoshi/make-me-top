FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/ /app
EXPOSE 8107
ENTRYPOINT ["java","-jar","/app/personal-cabinet-balancer-service-1.0-SNAPSHOT.jar"]
