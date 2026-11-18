package com.digi.uniprr.service;

public interface TokenExpireService {
    String tokenExpired();

    int checkIfExpired(String accessToken);

    int checkIfExpiredInDB(String accessToken);
}
