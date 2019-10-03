# Avid News Feed 

[![Build Status](https://travis-ci.org/iobruno/avid-news-feed.svg?branch=master)](https://travis-ci.org/iobruno/avid-news-feed)
[![codecov](https://codecov.io/gh/iobruno/avid-news-feed/branch/master/graph/badge.svg)](https://codecov.io/gh/iobruno/avid-news-feed)

## Up and Running

**Requirements**
- Kotlin 1.3.+
- JDK 8+

**Building**

It can be built with your gradle dist through the build.gradle file,
But just in case, "batteries" are included in pkg as well. Here's how to build with it:

```
> cd ${PROJECT_ROOT}
> ./gradlew build
> cp ${PROJECT_ROOT}/build/libs/avid-news-feed-1.0-SNAPSHOT.jar ${DEPLOY_DIR}
> java -jar ${DEPLOY_DIR}//avid-news-feed-1.0-SNAPSHOT.jar
```

OR for testing purposes-only, you can start it with `:bootRun` task

```
> cd ${PROJECT_ROOT}
> ./gradlew :bootRun
```
**Testing**
```
./gradlew test
```

## API Docs

Postman Collection:
https://www.getpostman.com/collections/96bd0fdffd578b98ecd2

