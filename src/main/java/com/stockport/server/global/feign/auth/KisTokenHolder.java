package com.stockport.server.global.feign.auth;

import com.stockport.server.global.feign.dto.KisTokenResponse;
import com.stockport.server.global.feign.client.KisAuthClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisTokenHolder {

    private final KisAuthClient kisAuthClient;
    private final KisProperties kisProperties;

    private volatile String accessToken;
    private volatile Instant expiresAt;

    @PostConstruct
    public void init() {
        refreshToken();
    }

    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            refreshToken();
        }
        return accessToken;
    }

    public synchronized void refreshToken() {
        KisTokenResponse response = kisAuthClient.issueToken(Map.of(
                "grant_type", "client_credentials",
                "appkey", kisProperties.getAppKey(),
                "appsecret", kisProperties.getAppSecret()
        ));

        this.accessToken = response.getAccessToken();
        this.expiresAt = LocalDateTime.parse(response.getExpiredAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();

//        String expiresValue = response.getExpiresIn();
//
//        try {
//            // 숫자형 (초 단위)인 경우
//            long expiresInSec = Long.parseLong(expiresValue);
//            this.expiresAt = Instant.now().plus(expiresInSec - 300, ChronoUnit.SECONDS);
//        } catch (NumberFormatException e) {
//            // 날짜 문자열인 경우
//            this.expiresAt = Instant.parse(
//                    expiresValue.replace(" ", "T") + "Z"
//            );
//        }
//
        log.info("KIS access token issued, valid until {}", expiresAt);
    }

    @Scheduled(fixedDelay = 23 * 60 * 60 * 1000) // 23시간마다
    public void scheduledRefresh() {
        refreshToken();
    }

    public synchronized void forceRefresh() { // 강제 갱신
        log.warn("[KIS] Manual token refresh requested by admin/service");
        refreshToken();
    }
}