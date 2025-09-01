package com.neojen.tft_share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class TftShareApplication {

    public static void main(String[] args) {

        // env 파일 선택: ENVIRONMENT 환경변수에 따라 dev/prod 결정
        String envType = System.getenv("ENVIRONMENT"); // 운영체제 ENVIRONMENT 우선
        if (envType == null || envType.isEmpty()) {
            envType = "dev"; // 기본값 dev
        }

        String dotenvFile = ".env." + envType;

        // 2Dotenv 로드
        Dotenv dotenv = Dotenv.configure()
                               .filename(dotenvFile)
                               .ignoreIfMissing()
                               .load();

        // 3환경 변수 읽기
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String googleClientId = dotenv.get("GOOGLE_CLIENT_ID");
        String googleClientSecret = dotenv.get("GOOGLE_CLIENT_SECRET");
        String jwtSecret = dotenv.get("JWT_SECRET");
        String riotApiKey = dotenv.get("RIOT_API_KEY");
        String awsAccessKey = dotenv.get("AWS_ACCESS_KEY");
        String awsSecretKey = dotenv.get("AWS_SECRET_KEY");
        
        System.setProperty("DB_USERNAME", dbUsername);
        System.setProperty("DB_PASSWORD", dbPassword);
        System.setProperty("GOOGLE_CLIENT_ID", googleClientId);
        System.setProperty("GOOGLE_CLIENT_SECRET", googleClientSecret);
        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("RIOT_API_KEY", riotApiKey);
        System.setProperty("AWS_ACCESS_KEY", awsAccessKey);
        System.setProperty("AWS_SECRET_KEY", awsSecretKey);

        // 5️⃣ SpringApplication 실행 + profile 적용
        SpringApplication app = new SpringApplication(TftShareApplication.class);
        app.setAdditionalProfiles(envType); // dev / prod profile 적용
        app.run(args);
    }
}