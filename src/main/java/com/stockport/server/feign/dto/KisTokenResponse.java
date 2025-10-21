package com.stockport.server.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private String expiresIn; // 토큰 만료 시간 (초 단위)

    @JsonProperty("access_token_token_expired")
    private String expiredAt; // 토큰 만료 시각
}