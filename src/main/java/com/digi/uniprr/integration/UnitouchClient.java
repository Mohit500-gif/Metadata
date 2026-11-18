package com.digi.uniprr.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Slf4j
@Component
public class UnitouchClient {

    private final RestTemplate restTemplate;

    @Value("${unitouch.url}")
    private String unitouchBaseUrl;

    public UnitouchClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void updateEmailInUnitouch(String oldEmail, String newEmail, List<String> manuscriptCodes) {
        try {
            String url = unitouchBaseUrl + "/api/unitouch/email-update/update";
            Map<String, Object> payload = new HashMap<>();
            payload.put("oldEmail", oldEmail);
            payload.put("newEmail", newEmail);
            payload.put("manuscriptCodes", manuscriptCodes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            logger.info("UniTouch update response: {}", response.getBody());
        } catch (Exception ex) {
            logger.error("Error calling UniTouch API: {}", ex.getMessage());
        }
    }
}

