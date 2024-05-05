### Hexlet tests and linter status:
[![Actions Status](https://github.com/kitdim/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/kitdim/java-project-99/actions)
[![Actions Status](https://github.com/kitdim/java-project-99/actions/workflows/java_ci.yml/badge.svg)](https://github.com/kitdim/java-project-99/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/f805494fac248db0bea3/maintainability)](https://codeclimate.com/github/kitdim/java-project-99/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/f805494fac248db0bea3/test_coverage)](https://codeclimate.com/github/kitdim/java-project-99/test_coverage)

# Task Manager

Task Manager is a system for task managing like [Redmine](http://www.redmine.org). You can create tasks, set assigners and change its statuses. Registration and authentication are required.

[Click here](https://java-project-99-pbef.onrender.com/) to try it, or [here](https://java-project-99-pbef.onrender.com/swagger-ui/index.html) if you want to explore an interactive documentation.

## Local start

If you want to start this project locally, enter this command:

```bash
make start
```

Then open http://localhost:8080 on your browser and enter admin details:

```
Username: hexlet@example.com
Password: qwerty
```

## Stack

* Java
* Spring Boot
* Spring Boot Security
* Databases: PostgreSQL, H2
* MapStruct
* JUnit5
* GNU Make
* PaaS Render