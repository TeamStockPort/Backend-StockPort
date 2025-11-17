package com.stockport.server.global.feign.adaptor;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.client.KisStockPriceClient;
import com.stockport.server.global.feign.dto.KisMultieStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisStockPriceAdaptor {
    private final KisStockPriceClient kisStockPriceClient;
    private final KisTokenHolder tokenHolder;
    private final ApiCallAdaptor apiCallAdaptor;

    public KisStockCurrentPrice getStockCurrentPrice(String stockCode) {
        try {
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
                                    startDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                                    endDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                            "D",
                            "0"
                            )
                    );

            if (!response.getResultCode().equals("0")) {
                throw new GeneralException(ErrorStatus.FEIGN_ERROR);
            }

            log.info("[KIS] 기간별 주가 조회 성공: {} ({} ~ {}) {}개 조회", stockCode, startDate, endDate, response.getOutput2().size());
            return response;

        } catch (Exception e) {
            log.error("[KIS] 기간별 주가 조회 실패 (stockCode={}): {}", stockCode, e.getMessage());
            throw new GeneralException(ErrorStatus.FEIGN_ERROR);
        }
    }

    public KisResponseWrapper<List<KisMultieStockCurrentPrice>> getMultiStockCurrentPrice(List<String> stockCodes) {
        try {
            String token = "Bearer " + tokenHolder.getAccessToken();

            var response =
                    apiCallAdaptor.callWithWait(() -> kisStockPriceClient.getMultiStockPrice(
                            "application/json; charset=utf-8",
                            token,
                            tokenHolder.getAppKey(),
                            tokenHolder.getAppSecret(),
                            "FHKST11300006",
                            "P",
                            "J",
                            "J", "J", "J", "J", "J", "J", "J", "J", "J", "J",
                            "J", "J", "J", "J", "J", "J", "J", "J", "J", "J",
                            "J", "J", "J", "J", "J", "J", "J", "J", "J",
                            stockCodes.size() > 0 ? stockCodes.get(0) : "", stockCodes.size() > 1 ? stockCodes.get(1) : "", stockCodes.size() > 2 ? stockCodes.get(2) : "",
                            stockCodes.size() > 3 ? stockCodes.get(3) : "", stockCodes.size() > 4 ? stockCodes.get(4) : "", stockCodes.size() > 5 ? stockCodes.get(5) : "",
                            stockCodes.size() > 6 ? stockCodes.get(6) : "", stockCodes.size() > 7 ? stockCodes.get(7) : "", stockCodes.size() > 8 ? stockCodes.get(8) : "",
                            stockCodes.size() > 9 ? stockCodes.get(9) : "", stockCodes.size() > 10 ? stockCodes.get(10) : "", stockCodes.size() > 11 ? stockCodes.get(11) : "",
                            stockCodes.size() > 12 ? stockCodes.get(12) : "", stockCodes.size() > 13 ? stockCodes.get(13) : "", stockCodes.size() > 14 ? stockCodes.get(14) : "",
                            stockCodes.size() > 15 ? stockCodes.get(15) : "", stockCodes.size() > 16 ? stockCodes.get(16) : "", stockCodes.size() > 17 ? stockCodes.get(17) : "",
                            stockCodes.size() > 18 ? stockCodes.get(18) : "", stockCodes.size() > 19 ? stockCodes.get(19) : "", stockCodes.size() > 20 ? stockCodes.get(20) : "",
                            stockCodes.size() > 21 ? stockCodes.get(21) : "", stockCodes.size() > 22 ? stockCodes.get(22) : "", stockCodes.size() > 23 ? stockCodes.get(23) : "",
                            stockCodes.size() > 24 ? stockCodes.get(24) : "", stockCodes.size() > 25 ? stockCodes.get(25) : "", stockCodes.size() > 26 ? stockCodes.get(26) : "",
                            stockCodes.size() > 27 ? stockCodes.get(27) : "", stockCodes.size() > 28 ? stockCodes.get(28) : "", stockCodes.size() > 29 ? stockCodes.get(29) : ""
                    ));

            if (!response.getResultCode().equals("0")) {
                throw new GeneralException(ErrorStatus.FEIGN_ERROR);
            }

            log.info("[KIS] 멀티 주가 조회 성공");
            return response;

        } catch (Exception e) {
            log.error("[KIS] 멀티 주가 조회 실패: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.FEIGN_ERROR);
        }
    }
}