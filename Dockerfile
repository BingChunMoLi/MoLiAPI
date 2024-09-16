FROM maven:3.9.9-eclipse-temurin-17-alpine as builder
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY lombok.config .
RUN --mount=type=cache,target=/root/.m2 mvn install -DskipTests
RUN java -Djarmode=tools -jar target/moliapi.jar extract --layers --destination extracted

FROM eclipse-temurin:17-jdk-alpine
MAINTAINER  BingChunMoLi <bingchunmoli@bingchunmoli.com>
VOLUME /soft/config
VOLUME /var/log/moliapi
EXPOSE 8090
RUN adduser --disabled-password  -h /soft/config/ -D -g api api
RUN mkdir -p /var/log/moliapi
RUN chown -R api:api /var/log/moliapi
USER api:api
COPY --from=builder /workspace/app/extracted/dependencies/ ./
COPY --from=builder /workspace/app/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/app/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/app/extracted/application/ ./
ENTRYPOINT ["sh", "-c" ,"java -Djava.security.egd=file:/dev/urandom ${JVM_OPTS} -jar moliapi.jar ${0} ${@}"]
