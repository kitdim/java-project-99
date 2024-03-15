FROM gradle:8.4.0-jdk20

WORKDIR /app

COPY / .

RUN gradle installDist

CMD ./build/install/java-project-99/bin/java-project-99