package com.example.demo.Model;

import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public void updateTokens(User user) {
        setAccessToken(user.getAccessToken());
        setRefreshToken(user.getRefreshToken());
    }

}
