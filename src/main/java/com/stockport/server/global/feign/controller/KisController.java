package com.stockport.server.global.feign.controller;

import com.stockport.server.global.feign.service.KisAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kis")
public class KisController {

    private final KisAuthService kisAuthService;

    @PostMapping("/token/refresh")
    public ResponseEntity<String> refreshTokenManually() {
        kisAuthService.forceIssueNewToken();
        return ResponseEntity.ok("Access token reissued successfully.");
    }
}
