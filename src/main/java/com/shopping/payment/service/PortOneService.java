package com.shopping.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PortOneService {

    @Value("${portone.api.key:YOUR_API_KEY}")
    private String apiKey;

    @Value("${portone.api.secret:YOUR_API_SECRET}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PORTONE_API_URL = "https://api.iamport.kr";

    /**
     * 포트원 인증 토큰 발급
     */
    public String getToken() {
        String url = PORTONE_API_URL + "/users/getToken";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> request = Map.of(
            "imp_key", apiKey,
            "imp_secret", apiSecret
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if ((Integer) responseBody.get("code") == 0) {
                Map<String, String> responseData = (Map<String, String>) responseBody.get("response");
                return responseData.get("access_token");
            }
        }
        throw new RuntimeException("Failed to get PortOne token");
    }

    /**
     * 결제 단건 조회
     */
    public Map<String, Object> getPaymentData(String impUid, String token) {
        String url = PORTONE_API_URL + "/payments/" + impUid;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if ((Integer) responseBody.get("code") == 0) {
                return (Map<String, Object>) responseBody.get("response");
            }
        }
        throw new RuntimeException("Failed to get payment data from PortOne");
    }
}
