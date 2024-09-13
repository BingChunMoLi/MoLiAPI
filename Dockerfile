FROM maven:3.9.9-eclipse-temurin-17-alpine as build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/home/cnb/.m2 mvn install -DskipTests
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:17-jdk-alpine
MAINTAINER  BingChunMoLi <bingchunmoli@bingchunmoli.com>
VOLUME /soft/config
EXPOSE 8090
RUN addgroup -S api && adduser -S api -G api
USER api:api
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /soft/lib
COPY --from=build ${DEPENDENCY}/META-INF /soft/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /soft
ENTRYPOINT ["sh", "-c" ,"java -cp soft:soft/lib/* -Djava.security.egd=file:/dev/urandom ${JAVA_OPTS} ApiApplication ${0} ${@}"]
