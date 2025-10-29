package com.stockport.server.global.feign.adaptor;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.client.KisIndexClient;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.KisIndexPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisIndexPriceAdaptor {
    private final KisIndexClient kisIndexClient;
    private final KisTokenHolder tokenHolder;

    public KisIndexCurrentPrice getIndexCurrentPrice(String indexCode) {
        try {
            log.info("[KIS] 지수 조회 요청: indexCode={}", indexCode);

            String token = "Bearer " + tokenHolder.getAccessToken();

            KisResponseWrapper<KisIndexCurrentPrice> response = kisIndexClient.getIndexPrice(
                    "application/json; charset=utf-8",
                    token,
                    tokenHolder.getAppKey(),
                    tokenHolder.getAppSecret(),
                    "FHKUP03500100",
                    "P",
                    "U",
                    indexCode
            );

            if (!"0".equals(response.getResultCode())) {
                log.error("[KIS] 지수 조회 실패 (resultCode={}): {}", response.getResultCode(), response.getMessage());
                throw new GeneralException(ErrorStatus.FEIGN_ERROR);
            }

            KisIndexCurrentPrice price = response.getOutput();
            log.info("[KIS] 지수 조회 성공: {} / 현재가={}", indexCode, price.getCurrentPrice());
            return price;

        } catch (Exception e) {
            log.error("[KIS] 지수 조회 중 예외 발생 (indexCode={}): {}", indexCode, e.getMessage());
            throw new GeneralException(ErrorStatus.FEIGN_ERROR);
        }
    }

    public List<KisIndexPeriodPrice> getIndexPeriodPrice(String indexCode, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("[KIS] 업종 기간별 시세 조회 요청: indexCode={}, start={}, end={}",
                    indexCode, startDate, endDate);

            String token = "Bearer " + tokenHolder.getAccessToken();

            var response = kisIndexClient.getIndexPeriodPrice(
                            "application/json; charset=utf-8",
                            token,
                            tokenHolder.getAppKey(),
                            tokenHolder.getAppSecret(),
                            "FHKUP03500100",
                            "P",
                            "U",
                            indexCode,
                            startDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                            endDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                            "D"
                    );

            if (!"0".equals(response.getResultCode())) {
                log.error("[KIS] 업종 기간별 시세 조회 실패 (code={}, msg={})",
                        response.getResultCode(), response.getMessage());
                throw new GeneralException(ErrorStatus.FEIGN_ERROR);
            }

            log.info("[KIS] 업종 기간별 시세 조회 성공: indexCode={}, 데이터 개수={}",
                    indexCode, response.getOutput2() != null ? response.getOutput2().size() : 0);

            return response.getOutput2();

        } catch (Exception e) {
            log.error("[KIS] 업종 기간별 시세 조회 중 예외 발생 (indexCode={}): {}", indexCode, e.getMessage());
            throw new GeneralException(ErrorStatus.FEIGN_ERROR);
        }
    }
}