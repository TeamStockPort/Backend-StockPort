package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class KisIndexPeriodPrice {

    @JsonProperty("stck_bsop_date")
    private String baseDate;        // 기준일 (YYYYMMDD)

    @JsonProperty("bstp_nmix_prpr")
    private String closePrice;      // 종가

    @JsonProperty("bstp_nmix_oprc")
    private String openPrice;       // 시가

    @JsonProperty("bstp_nmix_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("bstp_nmix_lwpr")
    private String lowPrice;        // 저가

    private BigDecimal caculateChangeAmount(String closePrice, BigDecimal prevClosePrice) {
        BigDecimal clpr = KisParsingUtils.parseBigDecimalSafe(closePrice);
        return clpr.subtract(prevClosePrice);
    }

    private BigDecimal caculateChangeRate(String closePrice, BigDecimal prevClosePrice) {
        try {
            BigDecimal changeAmount = caculateChangeAmount(closePrice, prevClosePrice);
            return changeAmount.multiply(BigDecimal.valueOf(100)).divide(prevClosePrice, 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }

    public IndexData toEntity(BigDecimal prevClosePrice, MarketType marketType) {
        return IndexData.builder()
                .marketType(marketType)
                .baseDate(KisParsingUtils.parseDateSafe(this.baseDate))
                .closePrice(KisParsingUtils.parseBigDecimalSafe(this.closePrice))
                .openPrice(KisParsingUtils.parseBigDecimalSafe(this.openPrice))
                .highPrice(KisParsingUtils.parseBigDecimalSafe(this.highPrice))
                .lowPrice(KisParsingUtils.parseBigDecimalSafe(this.lowPrice))
                .changeAmount(caculateChangeAmount(this.closePrice, prevClosePrice))
                .changeRate(caculateChangeRate(this.closePrice, prevClosePrice))
                .build();
    }
}
