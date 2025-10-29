package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
                .openPrice(KisParsingUtils.parseIntSafe(this.openPrice))
                .currentPrice(KisParsingUtils.parseIntSafe(this.currentPrice))
                .highPrice(KisParsingUtils.parseIntSafe(this.highPrice))
                .lowPrice(KisParsingUtils.parseIntSafe(this.lowPrice))
                .changeAmount(KisParsingUtils.parseIntSafe(this.changeAmount))
                .changeRate(KisParsingUtils.parseDoubleSafe(this.changeRate))
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
