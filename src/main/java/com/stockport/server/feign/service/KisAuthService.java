package com.stockport.server.feign.service;

import com.stockport.server.feign.client.KisAuthClient;
import com.stockport.server.feign.config.KisProperties;
import com.stockport.server.feign.dto.KisTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KisAuthService {
    private final KisProperties kisProperties;
    private final KisAuthClient kisAuthClient;

    public String getAccessToken() {
        KisTokenResponse response = kisAuthClient.issueToken(Map.of(
                "grant_type", "client_credentials",
                "appkey", kisProperties.getAppKey(),
                "appsecret", kisProperties.getAppSecret()
        ));
        return response.getAccessToken();
    }
}
