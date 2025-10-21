package com.stockport.server.feign.controller;

import com.stockport.server.feign.service.KisAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kis")
public class KisController {

    private final KisAuthService kisAuthService;

    @GetMapping("/token")
    public String getToken() {
        return kisAuthService.getAccessToken();
    }
}
