# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# JAR 파일을 컨테이너로 복사
ARG JAR_FILE=build/libs/improve_Fream_Back-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 포트 설정
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]