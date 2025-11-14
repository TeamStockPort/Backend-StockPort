package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.stock.entity.StockCurrentPrice;
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

    public StockCurrentPrice toEntity(LocalDate baseDate) {
        return StockCurrentPrice.builder()
                .baseDate(baseDate)
                .currentPrice(KisParsingUtils.parseBigDecimalSafe(currentPrice))
                .openPrice(KisParsingUtils.parseBigDecimalSafe(openPrice))
                .highPrice(KisParsingUtils.parseBigDecimalSafe(highPrice))
                .lowPrice(KisParsingUtils.parseBigDecimalSafe(lowPrice))
                .changeAmount(KisParsingUtils.parseBigDecimalSafe(changeAmount))
                .changeRate(KisParsingUtils.parseBigDecimalSafe(changeRate))
                .build();
    }
}
