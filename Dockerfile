# 基础镜像
FROM eclipse-temurin:21-alpine

# 作者信息
MAINTAINER  BingChunMoLi <bingchunmoli@foxmail.com>

USER root

# 添加一个存储空间
VOLUME /root/.api/

# 暴露8090端口
EXPOSE 8090

# 添加变量，如果使用dockerfile-maven-plugin，则会自动替换这里的变量内容
ARG JAR_FILE

# 往容器中添加jar包
ADD /target/${JAR_FILE} /root/.api/app.jar

# 启动镜像自动运行程序
ENTRYPOINT ["sh", "-c" ,"java -Djava.security.egd=file:/dev/urandom ${JAVA_OPTS} -jar /root/.api/app.jar ${0} ${@}"]
