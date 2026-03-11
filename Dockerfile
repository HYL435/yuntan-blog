# 第一阶段：构建镜像
FROM maven:3.9.12-eclipse-temurin-21 AS builder

WORKDIR /build

# 接收从 docker-compose 传来的微服务名称
ARG SERVICE_NAME

# 1. 拷贝根 POM
COPY pom.xml .

# 2. 拷贝所有公共模块 POM（必须先拷贝）
COPY blog-common/pom.xml ./blog-common/
COPY blog-api/pom.xml ./blog-api/

# 3. 拷贝【所有6个微服务】的 POM（关键！无论构建哪个服务都要全拷贝）
COPY blog-gateway/pom.xml ./blog-gateway/
COPY blog-user-service/pom.xml ./blog-user-service/
COPY blog-article-service/pom.xml ./blog-article-service/
COPY blog-comment-service/pom.xml ./blog-comment-service/
COPY blog-notify-service/pom.xml ./blog-notify-service/
COPY blog-manage-service/pom.xml ./blog-manage-service/

# 可选：下载依赖以利用 Docker 缓存（如果这一步报错，可直接注释掉）
# RUN mvn dependency:go-offline -DskipTests

# 4. 拷贝源代码（只需拷贝当前服务 + 公共模块即可，-am 会自动处理依赖）
COPY blog-common/src ./blog-common/src
COPY blog-api/src ./blog-api/src
COPY ${SERVICE_NAME}/src ./${SERVICE_NAME}/src

# 5. 核心：只构建指定的微服务及其依赖 (-pl 指定模块，-am 自动构建其依赖的 common/api)
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

# ==========================================

# 第二阶段：运行镜像（瘦身版）
FROM eclipse-temurin:21.0.9_10-jre-ubi10-minimal

WORKDIR /app

# 重新声明 ARG，因为 ARG 的作用域不能跨越 FROM 阶段
ARG SERVICE_NAME

# 设置时区（生产环境必备）
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置生产环境默认的 JVM 参数（可在 compose 中覆盖）
ENV JAVA_OPTS="-XX:+UseG1GC -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

# 使用通配符将构建好的 jar 包拷贝过来并重命名为 app.jar
COPY --from=builder /build/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar

# 暴露端口（这里作为占位，实际对外的端口在 compose 中控制）
EXPOSE 8080

# 启动命令
ENTRYPOINT sh -c "java $JAVA_OPTS -jar app.jar"