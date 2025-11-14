package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KisStockCurrentPrice {
    @JsonProperty("stck_oprc")
    private String openPrice;       // 시가

    @JsonProperty("stck_prpr")
    private String currentPrice;    // 현재가

    @JsonProperty("stck_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("stck_lwpr")
    private String lowPrice;        // 저가

    @JsonProperty("prdy_vrss")
    private String changeAmount;    // 등락폭

    @JsonProperty("prdy_ctrt")
    private String changeRate;      // 등락률

    public StockCurrentPrice toEntity() {
        return StockCurrentPrice.builder()
                .openPrice(KisParsingUtils.parseBigDecimalSafe(this.openPrice))
                .currentPrice(KisParsingUtils.parseBigDecimalSafe(this.currentPrice))
                .highPrice(KisParsingUtils.parseBigDecimalSafe(this.highPrice))
                .lowPrice(KisParsingUtils.parseBigDecimalSafe(this.lowPrice))
                .changeAmount(KisParsingUtils.parseBigDecimalSafe(this.changeAmount))
                .changeRate(KisParsingUtils.parseBigDecimalSafe(this.changeRate))
                .build();
    }

    public static KisStockCurrentPrice create(String openPrice, String currentPrice, String highPrice, String lowPrice, String changeAmount, String changeRate) {
        return KisStockCurrentPrice.builder()
                .openPrice(openPrice)
                .currentPrice(currentPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .changeAmount(changeAmount)
                .changeRate(changeRate)
                .build();
    }
}
