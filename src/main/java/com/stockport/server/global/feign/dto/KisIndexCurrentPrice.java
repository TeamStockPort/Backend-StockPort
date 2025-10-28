package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @JsonProperty("prdy_vrss_sign")
    private String changeSign;      // 전일 대비 등락 부호

    @JsonProperty("bstp_nmix_oprc")
    private String openPrice;       // 시가

    @JsonProperty("bstp_nmix_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("bstp_nmix_lwpr")
    private String lowPrice;        // 저가

    private BigDecimal caculateSign(String sign, String amount) {
        BigDecimal amt = KisParsingUtils.parseBigDecimalSafe(amount);
        if (sign.equals("-")) {
            return amt.negate();
        }
        return amt;
    }

    public IndexData toEntity(MarketType marketType, LocalDate baseDate) {
        return IndexData.builder()
                .marketType(marketType)
                .baseDate(baseDate)
                .closePrice(KisParsingUtils.parseDoubleSafe(currentPrice))
                .openPrice(KisParsingUtils.parseDoubleSafe(openPrice))
                .highPrice(KisParsingUtils.parseDoubleSafe(highPrice))
                .lowPrice(KisParsingUtils.parseDoubleSafe(lowPrice))
                .changeAmount(caculateSign(changeSign, changeAmount))
                .changeRate(caculateSign(changeSign, changeRate))
                .build();
    }
}