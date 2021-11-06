# 基础镜像
FROM openjdk:8-jdk-alpine

# 作者信息
MAINTAINER  BingChunMoLi <bingchunmoli@foxmail.com>

# 添加一个存储空间
VOLUME /api

# 暴露8090端口
EXPOSE 8090

# 添加变量，如果使用dockerfile-maven-plugin，则会自动替换这里的变量内容
ARG JAR_FILE

# 往容器中添加jar包
ADD /target/${JAR_FILE} /api/app.jar

# 启动镜像自动运行程序
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/api/app.jar"]