# Avid News Feed 
[![Build Status](https://travis-ci.org/iobruno/avid-news-feed.svg?branch=master)](https://travis-ci.org/iobruno/avid-news-feed)
[![codecov](https://codecov.io/gh/iobruno/avid-news-feed/branch/master/graph/badge.svg)](https://codecov.io/gh/iobruno/avid-news-feed)

**Avid News Feed** is an open-source project, that I built on my free time, to develop a REST API on top of Kotlin, 
with the Spring Stack. It can be used as a reference or starting point for anyone playing around with **Kotlin** and **Spring**

## Tech Stack
- Kotlin 1.3.+
- Spring Framework (Spring Boot, Spring Data)
- Gradle

## Application Design

### Layers
- **Controller**: Spring Controllers and endpoints
- **Model**: Domain model classes
- **Service**: Service layer to handle search and other CRUD operations (CRUDL)
- **Repository**: Handles DB transactions, and Specification Pattern/Predicates for CriteriaQuery

## Up and Running 

**Linux | macOS:**
```
./gradlew build
cp ${PROJECT_ROOT_DIR}/build/libs/avid-news-feed-1.0.jar ${TARGET_DIR}
java -jar ${TARGET_DIR}/avid-news-feed-1.0.jar
```

**Windows:**
```
gradlew.bat build
copy ${PROJECT_ROOT_DIR}\build\libs\avid-news-feed-1.0.jar ${TARGET_DIR}
java -jar ${TARGET_DIR}\avid-news-feed-1.0.jar
```

## Tests and Coverage

**Linux | macOS:**
```
./gradlew test
```

**Windows:**
```
gradlew.bat test
```

### Postman

Public Collection:
https://www.getpostman.com/collections/96bd0fdffd578b98ecd2

## TODO:
- [x] Automated builds with a CI (Travis | CircleCI)
- [x] Code Coverage check
- [ ] Dockerize app
- [ ] Ship it with GraalVM
