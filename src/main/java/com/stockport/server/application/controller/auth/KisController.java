package com.stockport.server.application.controller.auth;

import com.stockport.server.application.service.auth.KisAuthService;
import com.stockport.server.global.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kis")
public class KisController {

    private final KisAuthService kisAuthService;

    @PostMapping("/token/refresh")
    public ApiResponse<String> refreshTokenManually() {
        kisAuthService.forceIssueNewToken();
        return ApiResponse.onSuccess("KIS token refreshed successfully.");
    }
}
