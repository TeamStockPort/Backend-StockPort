package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KisStockPeriodPriceResponse {
    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String message;

    @JsonProperty("output")
    private List<KisStockPriceResponse> stockPriceList;
}
