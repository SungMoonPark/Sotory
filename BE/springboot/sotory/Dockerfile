# 1단계: 빌드용 이미지
FROM gradle:8.6-jdk17 AS builder

WORKDIR /app
COPY . .
RUN gradle build -x test

# 2단계: 실행 이미지
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/sotory-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]  # 또는 복사된 실제 .jar 파일명으로