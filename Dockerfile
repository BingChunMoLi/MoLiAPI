FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/home/cnb/.m2 chmod +x ./mvnw && ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jdk-alpine
MAINTAINER  BingChunMoLi <bingchunmoli@bingchunmoli.com>
VOLUME /tmp
EXPOSE 8090
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["sh", "-c" ,"java -cp app:app/lib/* -Djava.security.egd=file:/dev/urandom ${JAVA_OPTS} ApiApplication ${0} ${@}"]
