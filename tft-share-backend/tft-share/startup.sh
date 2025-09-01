#!/bin/bash

# TFT Share 애플리케이션 시작 스크립트
# 리눅스 서버에서 실행용

# 환경 변수 설정
export ENVIRONMENT=prod
export JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC -XX:+UseStringDeduplication"

# 로그 디렉토리 생성
mkdir -p logs

# 애플리케이션 실행
echo "Starting TFT Share application in production mode..."
echo "Environment: $ENVIRONMENT"
echo "Java Options: $JAVA_OPTS"

# JAR 파일 실행 (배포된 JAR 파일명에 맞게 수정)
java $JAVA_OPTS -jar tft-share-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
