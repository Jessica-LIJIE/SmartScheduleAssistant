FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# 配置Maven使用阿里云镜像（加速下载，解决网络问题）
RUN mkdir -p /root/.m2 && \
    cat > /root/.m2/settings.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven Central</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
EOF

# 复制pom文件
COPY pom.xml .

# 复制源代码
COPY src ./src

# 构建应用（Maven会自动下载依赖）
# 使用 -B 批处理模式，-DskipTests 跳过测试
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 复制jar文件
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

