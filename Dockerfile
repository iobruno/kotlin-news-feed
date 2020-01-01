# Stage-1: Building Container
FROM adoptopenjdk:11-jdk-hotspot AS builder

## Setting up Gradlew Distribution
COPY *.gradle.kts gradlew /app/
COPY gradle /app/gradle
WORKDIR /app 
RUN ./gradlew --version

## Build Main Application
COPY src /app/src
RUN ./gradlew --no-daemon clean build -x test

# Stage-2: Distribution Container
FROM adoptopenjdk:11-jre-hotspot AS app
COPY --from=builder /app/build/libs/*.jar /app/avid-news-feed.jar
ENTRYPOINT ["java", "-jar", "/app/avid-news-feed.jar"]

EXPOSE 8080