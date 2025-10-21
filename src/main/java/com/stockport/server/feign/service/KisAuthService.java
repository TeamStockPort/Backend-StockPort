package com.stockport.server.feign.service;

import com.stockport.server.feign.auth.KisTokenHolder;
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
