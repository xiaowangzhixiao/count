# 使用官方的 Maven 镜像作为构建环境
FROM maven:3.8.4-openjdk-11-slim AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 文件
COPY pom.xml .

# 下载依赖项
RUN mvn dependency:go-offline

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package

# 使用 OpenJDK 11 作为运行环境
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/count-server-1.0-SNAPSHOT.jar ./app.jar

# 暴露应用程序端口
EXPOSE 8080


RUN echo '#!/bin/sh\n\
java -Dstorage.type=${STORAGE_TYPE:-jdbc} \
     -Ddb.url=${DB_URL:-jdbc:mysql://localhost:3306/count} \
     -Ddb.user=${DB_USER:-default_user} \
     -Ddb.password=${DB_PASSWORD:-default_password} \
     -jar app.jar\n' > /app/start.sh && chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
