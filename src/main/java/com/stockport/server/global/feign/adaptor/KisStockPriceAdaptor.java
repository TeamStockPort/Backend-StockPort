package com.stockport.server.global.feign.adaptor;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.client.KisStockPriceClient;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisStockPriceAdaptor {
    private final KisStockPriceClient kisStockPriceClient;
    private final KisTokenHolder tokenHolder;
    private final ApiCallAdaptor apiCallAdaptor;

    public KisStockCurrentPrice getStockCurrentPrice(String stockCode) {
        try {
            log.info("[KIS] 주가 조회 요청: {}", stockCode);

            String token = "Bearer " + tokenHolder.getAccessToken();

            KisResponseWrapper<KisStockCurrentPrice> response =
                    apiCallAdaptor.callWithWait(() ->
                            kisStockPriceClient.getStockPrice(
                                    "application/json; charset=utf-8",
                                    token,
                                    tokenHolder.getAppKey(),
                                    tokenHolder.getAppSecret(),
                                    "FHKST01010100",
                                    "P",
                                    "J",
                                    stockCode
                            )
                    );

            if (!response.getResultCode().equals("0")) {
                throw new GeneralException(ErrorStatus.FEIGN_ERROR);
            }

            KisStockCurrentPrice price = response.getOutput();
            log.info("[KIS] 주가 조회 성공: {} / 현재가 {}", stockCode, price.getCurrentPrice());
            return price;

        } catch (Exception e) {
            log.error("[KIS] 주가 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            throw new GeneralException(ErrorStatus.FEIGN_ERROR);
        }
    }

    public KisPeriodResponseWrapper<KisStockCurrentPrice, KisStockPeriodPrice> getStockPeriodPrice(String stockCode, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("[KIS] 기간별 주가 조회 요청: {} ({} ~ {})", stockCode, startDate, endDate);

            String token = "Bearer " + tokenHolder.getAccessToken();

            var response =
                    apiCallAdaptor.callWithWait(() -> kisStockPriceClient.getPeriodPrice(
                            "application/json; charset=utf-8",
                            token,
                            tokenHolder.getAppKey(),
                            tokenHolder.getAppSecret(),
                            "FHKST03010100",
                            "P",
                            "J",
                            stockCode,
                            startDate.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                            endDate.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                            "P",
                            "1"
                            )
                    );

            if (!response.getResultCode().equals("0")) {
                throw new GeneralException(ErrorStatus.FEIGN_ERROR);
            }

            log.info("[KIS] 기간별 주가 조회 성공: {} ({} ~ {})", stockCode, startDate, endDate);
            return response;

        } catch (Exception e) {
            log.error("[KIS] 기간별 주가 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            throw new GeneralException(ErrorStatus.FEIGN_ERROR);
        }
    }
}