package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KisStockPeriodPriceWrapper {
    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String message;

    @JsonProperty("output1")
    private KisStockCurrentPrice stockCurrentPrice;

    @JsonProperty("output2")
    private List<KisStockPeriodPrice> stockPeriodPriceList;
}
