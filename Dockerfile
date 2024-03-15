FROM eclipse-temurin:20-jdk

ARG GRADLE_VERSION=8.4

RUN apt-get update && apt-get install -yq make unzip


WORKDIR /backend

COPY ./ .

RUN ./gradlew --no-daemon build

EXPOSE 8080

CMD ./gradlew bootRun --args='--spring.profiles.active=prod'