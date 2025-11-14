package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisIndexCurrentPrice {

    @JsonProperty("bstp_nmix_prpr")
    private String currentPrice;    // 현재 지수

    @JsonProperty("bstp_nmix_prdy_vrss")
    private String changeAmount;    // 전일 대비 등락폭

    @JsonProperty("bstp_nmix_prdy_ctrt")
    private String changeRate;      // 전일 대비 등락률 (%)

    @JsonProperty("bstp_nmix_oprc")
    private String openPrice;       // 시가

    @JsonProperty("bstp_nmix_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("bstp_nmix_lwpr")
    private String lowPrice;        // 저가

    public IndexData toEntity(MarketType marketType, LocalDate baseDate) {
        return IndexData.builder()
                .marketType(marketType)
                .baseDate(baseDate)
                .closePrice(KisParsingUtils.parseBigDecimalSafe(currentPrice))
                .openPrice(KisParsingUtils.parseBigDecimalSafe(openPrice))
                .highPrice(KisParsingUtils.parseBigDecimalSafe(highPrice))
                .lowPrice(KisParsingUtils.parseBigDecimalSafe(lowPrice))
                .changeAmount(KisParsingUtils.parseBigDecimalSafe(changeAmount))
                .changeRate(KisParsingUtils.parseBigDecimalSafe(changeRate))
                .build();
    }
}