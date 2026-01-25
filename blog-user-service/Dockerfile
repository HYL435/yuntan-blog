# 第一阶段：构建
FROM maven:3.9.12-eclipse-temurin-21 AS builder

WORKDIR /build

# 1. 复制所有 POM
COPY pom.xml .
COPY blog-common/pom.xml blog-common/pom.xml
COPY blog-user-service/pom.xml blog-user-service/pom.xml
COPY blog-gateway/pom.xml blog-gateway/pom.xml

# 2. 下载依赖
RUN mvn dependency:go-offline -DskipTests

# 3. 复制源代码
COPY blog-common/src blog-common/src
COPY blog-user-service/src blog-user-service/src

# 4. 构建
RUN mvn clean package -pl blog-user-service -am -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:21.0.9_10-jre-ubi10-minimal

WORKDIR /app

# --- 修改点在这里 ---
# 使用通配符 *.jar 来匹配 blog-user-service-1.0-SNAPSHOT.jar
COPY --from=builder /build/blog-user-service/target/blog-user-service-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]