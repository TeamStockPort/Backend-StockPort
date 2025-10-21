package com.stockport.server.feign.auth;

import com.stockport.server.feign.dto.KisTokenResponse;
import com.stockport.server.feign.client.KisAuthClient;
import com.stockport.server.feign.config.KisProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisTokenHolder {

    private final KisAuthClient kisAuthClient;
    private final KisProperties kisProperties;

    private volatile String accessToken;
    private volatile Instant expiresAt;

    /**
     * 애플리케이션 시작 시 바로 토큰 발급
     */
    @PostConstruct
    public void init() {
        refreshToken();
    }

    /**
     * Access Token 반환 (만료 시 자동 갱신)
     */
    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            refreshToken();
        }
        return accessToken;
    }

    /**
     * 실제 토큰 발급 로직
     */
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

    /**
     * 스케줄러로 23시간마다 자동 갱신
     */
    @Scheduled(fixedDelay = 23 * 60 * 60 * 1000) // 23시간마다
    public void scheduledRefresh() {
        refreshToken();
    }

    /**
     * 외부에서 강제로 AccessToken 갱신을 요청할 때 사용
     */
    public synchronized void forceRefresh() {
        log.warn("[KIS] Manual token refresh requested by admin/service");
        refreshToken();
    }
}