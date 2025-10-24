package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KisIndexCurrentPrice {

    @JsonProperty("bstp_nmix_prpr")
    private String currentPrice;    // 현재 지수

    @JsonProperty("bstp_nmix_prdy_vrss")
    private String changeAmount;    // 전일 대비 등락폭

    @JsonProperty("prdy_vrss_sign")
    private String changeSign;      // 전일 대비 등락부호 (1: 상승, 2: 하락, 3: 보합)

    @JsonProperty("bstp_nmix_prdy_ctrt")
    private String changeRate;      // 전일 대비 등락률 (%)

    @JsonProperty("bstp_nmix_oprc")
    private String openPrice;       // 시가

    @JsonProperty("dryy_bstp_nmix_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("bstp_nmix_lwpr")
    private String lowPrice;        // 저가
}
