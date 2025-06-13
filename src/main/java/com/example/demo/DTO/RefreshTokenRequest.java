package com.example.demo.DTO;

public class RefreshTokenRequest {

    private String refreshToken;
    private int expiresInMins;


    public RefreshTokenRequest(String refreshToken, int expiresInMins) {
        this.refreshToken = refreshToken;
        this.expiresInMins = expiresInMins;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getExpiresInMins() {
        return expiresInMins;
    }

    public void setExpiresInMins(int expiresInMins) {
        this.expiresInMins = expiresInMins;
    }

}
