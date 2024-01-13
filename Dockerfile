# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon clean build -x check
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM build as test
ARG MONGODB_USERNAME
ENV MONGODB_USERNAME=$MONGODB_USERNAME
ARG MONGODB_PASSWORD
ENV MONGODB_PASSWORD=$MONGODB_PASSWORD
ARG MONGODB_HOST
ENV MONGODB_HOST=$MONGODB_HOST
ARG SPRING_DATA_MONGODB_DATABASE
ENV SPRING_DATA_MONGODB_DATABASE=$SPRING_DATA_MONGODB_DATABASE
ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon -Dtest.ignoreFailures=true check

FROM test as prepare-sonar
ARG SONAR_TOKEN
ENV SONAR_TOKEN=$SONAR_TOKEN

FROM prepare-sonar as sonar
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon sonar

FROM prepare-sonar as sonar-pr
ARG sonar_pull_request_branch_name
ARG sonar_pull_request_key
ARG sonar_pull_request_base
RUN --mount=type=cache,target=/root/.gradle ./gradlew -Dsonar.pullrequest.branch=$sonar_pull_request_branch_name -Dsonar.pullrequest.base=$sonar_pull_request_base -Dsonar.pullrequest.key=$sonar_pull_request_key --no-daemon sonar

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
