package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KisStockPeriodPrice {
    @JsonProperty("stck_bsop_date")
    private String baseDate;        // 기준일 (YYYYMMDD)

    @JsonProperty("stck_oprc")
    private String openPrice;       // 시가

    @JsonProperty("stck_clpr")
    private String closePrice;      // 종가

    @JsonProperty("stck_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("stck_lwpr")
    private String lowPrice;        // 저가

    @JsonProperty("prdy_vrss_sign")
    private String changeSign;      // 등락부호

    @JsonProperty("prdy_vrss")
    private String changeAmount;    // 등락폭

    private BigDecimal caculateChangeAmount(String sign, String amount) {
        BigDecimal amt = KisParsingUtils.parseBigDecimalSafe(amount);
        if (sign.equals("-")) {
            return amt.negate();
        }
        return amt;
    }

    private BigDecimal caculateChangeRate(String closePrice, String sign, String amount) {
        try {
            BigDecimal clpr = KisParsingUtils.parseBigDecimalSafe(closePrice);
            BigDecimal chgAmt = caculateChangeAmount(sign, amount);
            BigDecimal prevClpr = clpr.subtract(chgAmt);

            return chgAmt.multiply(BigDecimal.valueOf(100)).divide(prevClpr, 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }

    public StockPrice toEntity() {
        return StockPrice.builder()
                .baseDate(KisParsingUtils.parseDateSafe(this.baseDate))
                .openPrice(KisParsingUtils.parseBigDecimalSafe(this.openPrice))
                .closePrice(KisParsingUtils.parseBigDecimalSafe(this.closePrice))
                .highPrice(KisParsingUtils.parseBigDecimalSafe(this.highPrice))
                .lowPrice(KisParsingUtils.parseBigDecimalSafe(this.lowPrice))
                .changeAmount(caculateChangeAmount(this.changeSign, this.changeAmount))
                .changeRate(caculateChangeRate(this.closePrice, this.changeSign, this.changeAmount))
                .build();
    }

    public static KisStockPeriodPrice create(String baseDate, String openPrice, String closePrice, String highPrice, String lowPrice, String changeSign, String changeAmount) {
        return KisStockPeriodPrice.builder()
                .baseDate(baseDate)
                .openPrice(openPrice)
                .closePrice(closePrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .changeSign(changeSign)
                .changeAmount(changeAmount)
                .build();
    }
}

