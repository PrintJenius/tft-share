package com.neojen.tft_share.dto;

public class LoginRequest {
    private String code;  // OAuth2 콜백 시 전달되는 인증 코드

    public LoginRequest() {}

    public LoginRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
