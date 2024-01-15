# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /workspace/app
COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon \
    clean build -x test -x integrationTest
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM build as test
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon \
    -Dtest.ignoreFailures=true \
    test

FROM test as integration-test
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SPRING_DATA_MONGODB_URI \
    --mount=type=secret,id=SPRING_DATA_MONGODB_DATABASE \
    export SPRING_DATA_MONGODB_URI=$(cat /run/secrets/SPRING_DATA_MONGODB_URI) && \
    export SPRING_DATA_MONGODB_DATABASE=$(cat /run/secrets/SPRING_DATA_MONGODB_DATABASE) && \
    ./gradlew --no-daemon \
    -Dtest.ignoreFailures=true \
    integrationTest

FROM integration-test as sonar
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SONAR_TOKEN \
    export SONAR_TOKEN=$(cat /run/secrets/SONAR_TOKEN) && \
    ./gradlew --no-daemon \
    sonar

FROM integration-test as sonar-pr
ARG sonar_pull_request_branch_name
ARG sonar_pull_request_key
ARG sonar_pull_request_base
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SONAR_TOKEN \
    export SONAR_TOKEN=$(cat /run/secrets/SONAR_TOKEN) && \
    ./gradlew --no-daemon \
    -Dsonar.pullrequest.branch=$sonar_pull_request_branch_name \
    -Dsonar.pullrequest.base=$sonar_pull_request_base \
    -Dsonar.pullrequest.key=$sonar_pull_request_key \
    sonar

FROM scratch as unit-test-results
COPY --from=test /workspace/app/build/test-results/test ./test-results/test

FROM scratch as integration-test-results
COPY --from=integration-test /workspace/app/build/integrationTest ./test-results/integrationTest

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
