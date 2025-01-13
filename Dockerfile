# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# Playwright (Chromium) 구동에 필요한 라이브러리 설치
RUN apt-get update && apt-get install -y \
    libgtk-3-0 libgbm-dev libnotify-dev libnss3 libxss1 libasound2 \
    fonts-liberation libappindicator3-1 xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# JAR 파일을 컨테이너로 복사
ARG JAR_FILE=build/libs/improve_Fream_Back-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 포트 설정
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]