FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
COPY lombok.config .
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests
RUN java -Djarmode=tools -jar target/moliapi.jar extract --layers --destination extracted

FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.authors="BingChunMoLi <bingchunmoli@bingchunmoli.com>"
EXPOSE 8090
VOLUME /soft/.api
WORKDIR /soft
RUN addgroup -S -g 10001 moliapi \
    && adduser -S -D -H -u 10001 -G moliapi moliapi \
    && mkdir -p /soft/.api/config /tmp \
    && chown -R moliapi:moliapi /soft /tmp
COPY --chown=moliapi:moliapi --from=builder /workspace/app/extracted/dependencies/ ./
COPY --chown=moliapi:moliapi --from=builder /workspace/app/extracted/spring-boot-loader/ ./
COPY --chown=moliapi:moliapi --from=builder /workspace/app/extracted/snapshot-dependencies/ ./
COPY --chown=moliapi:moliapi --from=builder /workspace/app/extracted/application/ ./
ENV MOLI_PATH=/soft/.api/ \
    SPRING_CONFIG_ADDITIONAL_LOCATION=optional:file:/soft/.api/config/
USER 10001:10001
ENTRYPOINT ["sh", "-c", "exec java -Djava.security.egd=file:/dev/urandom $JVM_OPTS -jar moliapi.jar \"$@\"", "--"]
