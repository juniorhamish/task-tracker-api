# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /workspace/app
COPY build/libs build/libs
RUN rm build/libs/*-plain.jar && rm build/libs/*-test-fixtures.jar && mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

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
