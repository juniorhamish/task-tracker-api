# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon clean build -x check
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM build as test
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=secret,id=MONGODB_USERNAME \
    --mount=type=secret,id=MONGODB_PASSWORD \
    --mount=type=secret,id=MONGODB_HOST \
    --mount=type=secret,id=SPRING_PROFILES_ACTIVE \
    --mount=type=secret,id=MONGODB_DATABASE \
    export MONGODB_USERNAME=$(cat /run/secrets/MONGODB_USERNAME) && \
    export MONGODB_PASSWORD=$(cat /run/secrets/MONGODB_PASSWORD) && \
    export MONGODB_HOST=$(cat /run/secrets/MONGODB_HOST) && \
    export SPRING_PROFILES_ACTIVE=$(cat /run/secrets/SPRING_PROFILES_ACTIVE) && \
    export MONGODB_DATABASE=$(cat /run/secrets/MONGODB_DATABASE) && \
    ./gradlew --no-daemon -Dtest.ignoreFailures=true check

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
