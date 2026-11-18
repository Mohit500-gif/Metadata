package com.digi.uniprr.config;

import lombok.Getter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpClientGenerator {

    @Getter
    public static HttpClient httpClient=HttpClientBuilder.create().build();

//    public static HttpClient getInstance()
//    {
//        return httpClient;
//    }
}
