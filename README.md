# Run instructions

stop running containers

```shell
docker compose down
```

build project

```shell
mvn -f ./person-service-grpc-common/pom.xml clean install
mvn clean package
mvn -f ./config-service/pom.xml clean package
mvn -f ./discovery-service/pom.xml clean package
mvn -f ./admin-service/pom.xml clean package
mvn -f ./gateway-service/pom.xml clean package
```

run docker compose

```shell
docker compose up -d --build
```
