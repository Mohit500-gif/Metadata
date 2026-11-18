package com.digi.uniprr.serviceImpl;

import com.digi.uniprr.config.HttpClientGenerator;
import com.digi.uniprr.model.UserLoggedInAccess;
import com.digi.uniprr.repository.TokenCheckRepo;
import com.digi.uniprr.service.TokenExpireService;
import com.digi.uniprr.utils.AppConstants;
import javaxt.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class TokenExpireServiceImpl implements TokenExpireService {

    @Autowired
    TokenCheckRepo tokenCheckRepo;

    @Override
    public String tokenExpired() {
        return "Token has expired!";
    }

    @Override
    public int checkIfExpired(String accessToken)
    {
        HttpPost httpRequest = new HttpPost(AppConstants.VALIDATION_URL);
        httpRequest.setHeader(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
        httpRequest.setHeader(AppConstants.AUTHORIZATION, accessToken);
        String payload = "{\"username\":\"\",\"password\":\"\",\"role\":\"\"}";
        try {
            httpRequest.setEntity(new StringEntity(payload));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HttpResponse httpResponse;
        try {
            httpResponse = HttpClientGenerator.httpClient.execute(httpRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        String jsonResponse;
        try {
            jsonResponse = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(String.valueOf(new JSONObject(jsonResponse).get("statusCode")));

    }

    @Override
    public int checkIfExpiredInDB(String accessToken) {

        UserLoggedInAccess userLoggedInAccess=tokenCheckRepo.getAccessToken(accessToken);
        if(userLoggedInAccess!=null && userLoggedInAccess.getAccessToken()!=null)
        {
            return 100;
        }
        else {
            return 400;
        }


    }
}
