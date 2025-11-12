package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class KisMultieStockCurrentPrice {
    @JsonProperty("inter2_oprc")
    private String openPrice;          // 시가

    @JsonProperty("inter2_prpr")
    private String currentPrice;       // 현재가

    @JsonProperty("inter2_hgpr")
    private String highPrice;          // 고가

    @JsonProperty("inter2_lwpr")
    private String lowPrice;           // 저가

    @JsonProperty("inter2_prdy_vrss")
    private String changeAmount;       // 등락폭

    @JsonProperty("prdy_ctrt")
    private String changeRate;         // 등락률

    public IndexData toEntity(MarketType marketType, LocalDate baseDate) {
        return IndexData.builder()
                .marketType(marketType)
                .baseDate(baseDate)
                .closePrice(KisParsingUtils.parseDoubleSafe(currentPrice))
                .openPrice(KisParsingUtils.parseDoubleSafe(openPrice))
                .highPrice(KisParsingUtils.parseDoubleSafe(highPrice))
                .lowPrice(KisParsingUtils.parseDoubleSafe(lowPrice))
                .changeAmount(KisParsingUtils.parseDoubleSafe(changeAmount))
                .changeRate(KisParsingUtils.parseDoubleSafe(changeRate))
                .build();
    }
}
