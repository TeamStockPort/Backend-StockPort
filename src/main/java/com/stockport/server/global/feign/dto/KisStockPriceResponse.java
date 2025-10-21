package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private LocalDate parseDateSafe(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }

    private Integer parseIntSafe(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }

    private BigDecimal parseDoubleSafe(String val) {
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }

    public StockPrice toEntity() {
        return StockPrice.builder()
                .baseDate(parseDateSafe(this.baseDate))
                .openPrice(parseIntSafe(this.openPrice))
                .closePrice(parseIntSafe(this.closePrice))
                .highPrice(parseIntSafe(this.highPrice))
                .lowPrice(parseIntSafe(this.lowPrice))
                .changeAmount(parseIntSafe(this.changeAmount))
                .changeRate(parseDoubleSafe(this.changeRate))
                .build();
    }
}
