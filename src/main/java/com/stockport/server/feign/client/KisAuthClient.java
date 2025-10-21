package com.stockport.server.feign.client;

import com.stockport.server.feign.dto.KisTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "kisAuthClient", url = "${kis.base-url}")
public interface KisAuthClient {
    @PostMapping(value = "/oauth2/tokenP", consumes = "application/json")
    KisTokenResponse issueToken(@RequestBody Map<String, String> body);
}
