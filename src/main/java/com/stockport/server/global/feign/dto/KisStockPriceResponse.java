package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KisStockPriceResponse {
    @JsonProperty("stck_bsop_date")
    private String baseDate;        // 기준일 (YYYYMMDD)

    @JsonProperty("stck_oprc")
    private String openPrice;       // 시가

    @JsonProperty("stck_prpr")
    private String closePrice;      // 종가

    @JsonProperty("stck_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("stck_lwpr")
    private String lowPrice;        // 저가

    @JsonProperty("prdy_vrss")
    private String changeAmount;    // 등락폭

    @JsonProperty("prdy_ctrt")
    private String changeRate;      // 등락률
}
