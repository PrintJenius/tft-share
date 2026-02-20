# 🎮 TFT Share
> **전략적 팀 전투(TFT) 플레이 영상 공유 및 커뮤니티 플랫폼**
>
> **React**와 **Spring Boot 3**를 기반으로 구축되었으며, **AWS 인프라**를 설계하고 **Google API**를 연동하여 실제 서비스가 가능하도록 구현한 프로젝트입니다.

---

## 🏗 System Architecture
가용성과 보안을 고려하여 설계된 AWS 기반 인프라 구조입니다.



- **Frontend**: `S3` + `CloudFront` (HTTPS 및 글로벌 캐싱 적용)
- **Backend**: `EC2` + `ALB` (Target Group 설정을 통한 효율적 라우팅)
- **Database**: `RDS (MySQL)` (Private Subnet/Security Group을 통한 DB 보안 강화)

---

## 🛠 Tech Stack

### Backend (Spring Boot 3.5.4)
- **Security**: `Spring Security`, `JJWT` (Stateless JWT 인증 구현)
- **API**: `Google OAuth 2.0`, `YouTube Data API v3`
- **Infrastructure**: `AWS SDK for Java (S3)`, `Dotenv-Java`
- **Database**: `Spring Data JPA`, `MySQL`

### Frontend (React)
- **Rendering**: CSR (Client Side Rendering)
- **Deployment**: Static Web Hosting on AWS S3

---

## 🔐 Deployment & Configuration (핵심 역량)

### 1. 환경 변수 보안 관리 (Environment Variables)
코드 보안을 위해 모든 민감 정보는 소스 코드와 완전히 분리하여 관리하였습니다.
- **배포 서버(EC2) 설정**: 서버 유출 시에도 API Key가 노출되지 않도록 서버의 `~/.bashrc`에 `export` 명령어로 환경 변수를 등록하여 관리하였습니다.
- **로컬 개발**: `.env` 파일을 활용하되, `.gitignore`를 통해 레포지토리 노출을 원천 차단하였습니다.

### 2. 주요 설정 항목
| 환경 변수명 | 용도 |
| :--- | :--- |
| `DB_URL` / `ID` / `PW` | AWS RDS 접속 자격 증명 |
| `GOOGLE_CLIENT_ID` / `SECRET` | Google 소셜 로그인 및 YouTube API 권한 |
| `AWS_ACCESS_KEY` / `SECRET` | S3 미디어 업로드용 IAM 계정 키 |

---

## 🔥 Engineering Challenge (Troubleshooting)

### CSR 구조에서의 외부 서비스(Google) 심사 이슈 해결
- **문제**: Google OAuth 심사 시, React CSR 특성상 초기 HTML에 콘텐츠가 없어 "개인정보처리방침 링크 없음"으로 반려됨.
- **분석**: 크롤링 봇이 JavaScript를 실행하지 않고 정적 HTML만 검사한다는 점을 파악.
- **해결**: S3의 `index.html`에 필수 링크를 직접 삽입하여 **Pre-rendering** 효과를 주었으며, `CloudFront Invalidation`을 통해 전 세계 엣지에 즉시 반영하여 심사를 통과함.



---

## 🚀 Future Roadmap
- **Next.js 전환**: SEO 최적화 및 초기 로딩 속도 향상을 위한 SSR 도입.
- **CI/CD 자동화**: GitHub Actions를 통한 빌드/배포 프로세스 자동화 구축.
