# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS gradle-base
RUN apk upgrade --update-cache --no-cache && apk add dos2unix && apk cache clean
WORKDIR /workspace/app
COPY ./gradlew ./
RUN dos2unix ./gradlew
COPY ./gradle ./gradle
RUN sed -i 's/all.zip/bin.zip/g' ./gradle/wrapper/gradle-wrapper.properties
COPY ./gradle.properties .
COPY ./settings.gradle .
COPY ./build.gradle .
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon \
    dependencies --stacktrace

FROM gradle-base AS build
COPY ./lombok.config .
COPY ./src ./src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon \
    build -x test -x integrationTest
RUN rm build/libs/*-plain.jar && mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

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
