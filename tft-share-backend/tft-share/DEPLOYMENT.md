# TFT Share 배포 환경 설정 가이드

## 🚀 동영상 업로드 배포 환경 설정

### 1. 환경 변수 설정

리눅스 서버에서 다음 환경 변수를 설정해야 합니다:

```bash
# /etc/environment 또는 ~/.bashrc에 추가
export ENVIRONMENT=prod

# 데이터베이스 설정
export DB_USERNAME=tft_share_user
export DB_PASSWORD=your_secure_password_here

# JWT 설정
export JWT_SECRET=your_very_long_and_secure_jwt_secret_key_here_make_it_at_least_256_bits

# Google OAuth 설정
export GOOGLE_CLIENT_ID=221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com
export GOOGLE_CLIENT_SECRET=your_google_client_secret_here

# AWS 설정
export AWS_ACCESS_KEY=your_aws_access_key_id
export AWS_SECRET_KEY=your_aws_secret_access_key

# Riot API 설정
export RIOT_API_KEY=your_riot_api_key_here
```

### 2. Google Cloud Console 설정

#### OAuth 2.0 클라이언트 ID 설정
1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. 프로젝트 선택
3. "API 및 서비스" > "사용자 인증 정보" 이동
4. OAuth 2.0 클라이언트 ID 편집
5. 승인된 리디렉션 URI에 다음 추가:
   - `http://localhost:8080/api/oauth2/callback` (개발용)
   - `http://tftshare.com:8080/api/oauth2/callback` (운영용)

#### YouTube Data API v3 활성화
1. "API 및 서비스" > "라이브러리" 이동
2. "YouTube Data API v3" 검색 및 활성화

### 3. 프론트엔드 환경 변수

프론트엔드 빌드 시 다음 환경 변수를 설정:

```bash
# 개발 환경
VITE_BACKEND_URL=http://localhost:8080
VITE_GOOGLE_CLIENT_ID=221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com

# 운영 환경
VITE_BACKEND_URL=https://tftshare.com:8080
VITE_GOOGLE_CLIENT_ID=221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com
```

### 4. 토큰 갱신 메커니즘

#### 자동 토큰 갱신
- `GoogleTokenRefreshService`가 Access Token 만료 시 자동으로 Refresh Token을 사용하여 갱신
- 사용자가 재로그인할 필요 없이 동영상 업로드 가능

#### 토큰 만료 시 처리
- 프론트엔드에서 "Google 인증이 만료되었습니다" 오류 감지
- 자동으로 로그아웃 처리 후 로그인 페이지로 이동

### 5. 보안 설정

#### CORS 설정
운영 환경에서는 특정 도메인만 허용하도록 설정:

```java
// WebConfig.java
.allowedOrigins("https://tftshare.com")  // 운영 환경 도메인만 허용
```

#### HTTPS 설정
운영 환경에서는 반드시 HTTPS 사용:

```nginx
# Nginx 설정 예시
server {
    listen 443 ssl;
    server_name tftshare.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 6. 모니터링 및 로깅

#### 로그 설정
운영 환경에서 상세한 로그 확인:

```bash
# 애플리케이션 로그 확인
tail -f logs/tft-share.log

# 시스템 로그 확인
journalctl -u tft-share -f
```

#### 헬스 체크
애플리케이션 상태 확인:

```bash
curl http://localhost:8080/
```

### 7. 문제 해결

#### 동영상 업로드 실패 시
1. 로그 확인: `tail -f logs/tft-share.log`
2. Google OAuth 토큰 상태 확인
3. 데이터베이스 연결 상태 확인
4. AWS S3 연결 상태 확인

#### 토큰 만료 문제
1. Refresh Token이 DB에 저장되어 있는지 확인
2. Google Cloud Console에서 OAuth 설정 확인
3. 환경 변수 설정 확인

### 8. 성능 최적화

#### JVM 옵션
```bash
export JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC -XX:+UseStringDeduplication"
```

#### 파일 업로드 설정
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 2000MB
      max-request-size: 2000MB
      location: ${java.io.tmpdir}
```

이 설정을 완료하면 배포 환경에서도 동영상 업로드가 정상적으로 작동합니다.

