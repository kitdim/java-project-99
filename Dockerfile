FROM eclipse-temurin:20-jdk

ARG GRADLE_VERSION=8.4

RUN apt-get update && apt-get install -yq make unzip

WORKDIR /backend

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew --no-daemon build

ENV JAVA_OPTS "-Xmx512M -Xms512M"

EXPOSE 8080

CMD java -jar -Dspring.profiles.active=prod build/libs/app-0.0.1-SNAPSHOT.jar