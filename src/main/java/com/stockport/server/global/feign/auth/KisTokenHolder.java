package com.stockport.server.global.feign.auth;

import com.stockport.server.global.feign.config.KisProperties;
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

        log.info("[Auth] 액세스 토큰이 발급되었습니다. \naccessToken: {} \n{} 까지 유효합니다.", accessToken, expiresAt);
    }

    @Scheduled(fixedDelay = 23 * 60 * 60 * 1000)    // 23시간마다 갱신
    public void scheduledRefresh() {
        refreshToken();
    }

    public synchronized void forceRefresh() {       // 강제 갱신
        log.warn("[Auth] 액세스 토큰이 강제 갱신됩니다.");
        refreshToken();
    }
}