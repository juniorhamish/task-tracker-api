# syntax=docker/dockerfile:experimental
FROM amazoncorretto:22-alpine-jdk AS prepare
RUN apk upgrade --update-cache --no-cache && apk add dos2unix && apk cache clean
WORKDIR /workspace/app
COPY ./gradle ./gradle
RUN sed -i 's/all.zip/bin.zip/g' ./gradle/wrapper/gradle-wrapper.properties
COPY ./gradlew .
RUN dos2unix ./gradlew
COPY ./gradle.properties .
COPY ./settings.gradle .
COPY ./build.gradle .

FROM prepare AS build
COPY ./lombok.config .
COPY ./src ./src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew \
    --no-daemon \
    clean build -x test -x integrationTest
RUN rm build/libs/*-plain.jar && mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM build AS test
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon \
    -Dtest.ignoreFailures=true \
    test

FROM test AS integration-test
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SPRING_DATA_MONGODB_URI \
    --mount=type=secret,id=SPRING_DATA_MONGODB_DATABASE \
    --mount=type=secret,id=BETTER_STACK_TOKEN \
    --mount=type=secret,id=OAUTH_MANAGEMENT_API_CLIENT_ID \
    --mount=type=secret,id=OAUTH_MANAGEMENT_API_CLIENT_SECRET \
    export SPRING_DATA_MONGODB_URI=$(cat /run/secrets/SPRING_DATA_MONGODB_URI) && \
    export SPRING_DATA_MONGODB_DATABASE=$(cat /run/secrets/SPRING_DATA_MONGODB_DATABASE) && \
    export BETTER_STACK_TOKEN=$(cat /run/secrets/BETTER_STACK_TOKEN) && \
    export OAUTH_MANAGEMENT_API_CLIENT_ID=$(cat /run/secrets/OAUTH_MANAGEMENT_API_CLIENT_ID) && \
    export OAUTH_MANAGEMENT_API_CLIENT_SECRET=$(cat /run/secrets/OAUTH_MANAGEMENT_API_CLIENT_SECRET) && \
    ./gradlew --no-daemon \
    -Dtest.ignoreFailures=true \
    integrationTest

FROM integration-test AS sonar
COPY .git ./.git
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SONAR_TOKEN \
    --mount=type=secret,id=GITHUB_TOKEN \
    export SONAR_TOKEN=$(cat /run/secrets/SONAR_TOKEN) && \
    export GITHUB_TOKEN=$(cat /run/secrets/GITHUB_TOKEN) && \
    ./gradlew --no-daemon \
    sonar

FROM integration-test AS sonar-pr
COPY .git ./.git
ARG sonar_pull_request_branch_name
ARG sonar_pull_request_key
ARG sonar_pull_request_base
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=SONAR_TOKEN \
    --mount=type=secret,id=GITHUB_TOKEN \
    export SONAR_TOKEN=$(cat /run/secrets/SONAR_TOKEN) && \
    export GITHUB_TOKEN=$(cat /run/secrets/GITHUB_TOKEN) && \
    ./gradlew --no-daemon \
    -Dsonar.pullrequest.branch="$sonar_pull_request_branch_name" \
    -Dsonar.pullrequest.base="$sonar_pull_request_base" \
    -Dsonar.pullrequest.key="$sonar_pull_request_key" \
    sonar

FROM scratch AS unit-test-results
COPY --from=test /workspace/app/build/test-results/test ./test-results/test

FROM scratch AS integration-test-results
COPY --from=integration-test /workspace/app/build/test-results/integrationTest ./test-results/integrationTest

FROM amazoncorretto:22-alpine-jdk
RUN addgroup -S dj && adduser -S dj -G dj
USER dj
EXPOSE 8080
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","uk.co.dajohnston.houseworkapi.HouseworkApiApplication"]
