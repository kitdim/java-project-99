.DEFAULT_GOAL := build-run

install:
	./gradlew installDist

run-dist:
	./build/install/app/bin/app

build:
	./gradlew build

run:
	./gradlew run

test:
	./gradlew test

lint:
	./gradlew checkstyleMain checkstyleTest

report:
	./gradlew jacocoTestReport

build-run: build run

.PHONY: build