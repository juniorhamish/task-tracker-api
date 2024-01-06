# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon clean build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM build as test
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon -Dtest.ignoreFailures=true test

FROM test as sonar
ARG SONAR_TOKEN
ENV SONAR_TOKEN=$SONAR_TOKEN
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon sonar

FROM scratch as results
COPY --from=test /workspace/app/build/test-results ./test-results

FROM amazoncorretto:21-alpine-jdk
RUN addgroup -S dj && adduser -S dj -G dj
USER dj
EXPOSE 8080
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","uk.co.dajohnston.houseworkapi.HouseworkApiApplication"]
