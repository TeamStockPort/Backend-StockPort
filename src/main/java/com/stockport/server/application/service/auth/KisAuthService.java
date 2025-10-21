package com.stockport.server.application.service.auth;

import com.stockport.server.global.feign.auth.KisTokenHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KisAuthService {
    private final KisTokenHolder kisTokenHolder;

    public void forceIssueNewToken() {
        kisTokenHolder.forceRefresh();
    }
}
