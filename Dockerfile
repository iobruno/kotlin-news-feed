##########################################
## Stage-1: Building Container
##########################################
FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-alpine AS builder

## Setting up Gradlew Distribution
COPY *.gradle.kts gradlew /app/
COPY gradle /app/gradle
WORKDIR /app 

## Application Build
COPY src /app/src
RUN ./gradlew --no-daemon clean build -x test

#########################################
# Stage-2: Distribution Container
#########################################
FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine AS application
COPY --from=builder /app/build/libs/*.jar /app/avid-news-feed.jar
ENTRYPOINT ["java", "-jar", "/app/avid-news-feed.jar"]

EXPOSE 8080
