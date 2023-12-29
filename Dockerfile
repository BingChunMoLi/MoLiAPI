# 基础镜像
FROM eclipse-temurin:21-alpine

# 作者信息
MAINTAINER  BingChunMoLi <bingchunmoli@foxmail.com>

USER root

# 添加一个存储空间
VOLUME /root/.api/

# 暴露8090端口
EXPOSE 8090

# 往容器中添加jar包
ADD /target/moliapi.jar /root/.api/app.jar

# 启动镜像自动运行程序
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/root/.api/app.jar"]
