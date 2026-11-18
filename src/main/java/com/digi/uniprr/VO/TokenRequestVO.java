package com.digi.uniprr.VO;

public class TokenRequestVO {


    String accessToken;

    @Override
    public String toString() {
        return "TokenRequestVO{" +
                "accessToken='" + accessToken + '\'' +
                '}';
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
