# 1. Build Stage
FROM gradle:jdk21 AS builder
WORKDIR /app

# 캐시 효율성을 위해 의존성 파일만 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle gradle
# 의존성 다운로드 (소스 코드 복사 전)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src src
RUN gradle bootJar --no-daemon

# 2. Run Stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 보안을 위해 루트가 아닌 사용자로 실행 (선택 사항이지만 권장됨)
# RUN groupadd -r spring && useradd -r -g spring spring
# USER spring

EXPOSE 8080

# JVM 옵션 설정 가능 (예: 메모리 설정)
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
