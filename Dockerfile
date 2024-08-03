# syntax=docker/dockerfile:experimental
FROM gradle:8.9-alpine AS gradle-base
ENV GRADLE_USER_HOME /root/.gradle
WORKDIR /workspace/app
COPY ./gradle.properties .
COPY ./settings.gradle .
COPY ./build.gradle .
RUN --mount=type=cache,target=/root/.gradle \
    gradle --no-daemon \
    dependencies --stacktrace

FROM gradle-base AS build
COPY ./lombok.config .
COPY ./src ./src
RUN --mount=type=cache,target=/root/.gradle \
    gradle --no-daemon \
    build -x test -x integrationTest
RUN rm build/libs/*-plain.jar && mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM build AS test
RUN --mount=type=cache,target=/root/.gradle \
    gradle --no-daemon \
    -Dtest.ignoreFailures=true \
    test

FROM test AS integration-test
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SPRING_DATA_MONGODB_URI \
    --mount=type=secret,id=SPRING_DATA_MONGODB_DATABASE \
    export SPRING_DATA_MONGODB_URI=$(cat /run/secrets/SPRING_DATA_MONGODB_URI) && \
    export SPRING_DATA_MONGODB_DATABASE=$(cat /run/secrets/SPRING_DATA_MONGODB_DATABASE) && \
    gradle --no-daemon \
    -Dtest.ignoreFailures=true \
    integrationTest

FROM integration-test AS sonar
COPY .git ./.git
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SONAR_TOKEN \
    export SONAR_TOKEN=$(cat /run/secrets/SONAR_TOKEN) && \
    gradle --no-daemon \
    sonar

FROM integration-test AS sonar-pr
ARG sonar_pull_request_branch_name
ARG sonar_pull_request_key
ARG sonar_pull_request_base
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SONAR_TOKEN \
    export SONAR_TOKEN=$(cat /run/secrets/SONAR_TOKEN) && \
    gradle --no-daemon \
    -Dsonar.pullrequest.branch="$sonar_pull_request_branch_name" \
    -Dsonar.pullrequest.base="$sonar_pull_request_base" \
    -Dsonar.pullrequest.key="$sonar_pull_request_key" \
    sonar

FROM scratch AS unit-test-results
COPY --from=test /workspace/app/build/test-results/test ./test-results/test

FROM scratch AS integration-test-results
COPY --from=integration-test /workspace/app/build/test-results/integrationTest ./test-results/integrationTest

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
