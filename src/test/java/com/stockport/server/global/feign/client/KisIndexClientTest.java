package com.stockport.server.global.feign.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.KisIndexPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class KisIndexClientTest {

    @Autowired
    private KisIndexClient kisIndexClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KisTokenHolder tokenHolder;

    @Test
    @DisplayName("KIS 인덱스(코스피) 조회 API를 실제 호출한다")
    void getKospiIndexPrice() throws JsonProcessingException {
        // given
        String token = tokenHolder.getAccessToken();
        String appKey = tokenHolder.getAppKey();
        String appSecret = tokenHolder.getAppSecret();

        String trId = "FHPUP02100000";   // 인덱스 조회용 TR ID
        String custType = "P";            // 개인 투자자
        String marketCode = "U";          // 국내 주식시장
        String indexCode = "0001";        // 코스피 지수 코드

        // when
        var response = kisIndexClient.getIndexPrice(
                "application/json; charset=utf-8",
                token,
                appKey,
                appSecret,
                trId,
                custType,
                marketCode,
                indexCode
        );

        // then
        // 전체 JSON 로그 출력
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        log.info("[Full JSON Response]\n{}", json);

        log.info("응답 코드: {}", response.getResultCode());
        log.info("응답 메시지: {}", response.getMessage());

        KisIndexCurrentPrice index = response.getOutput();
        if (index != null) {
            log.info("📊 [KOSPI 지수 조회 결과]");
            log.info("현재가: {}", index.getCurrentPrice());
            log.info("등락폭: {}", index.getChangeAmount());
            log.info("등락률: {}", index.getChangeRate());
            log.info("고가: {}", index.getHighPrice());
            log.info("저가: {}", index.getLowPrice());
        }

        assert response.getResultCode().equals("0") : "API 호출 실패";
    }

    @Test
    @DisplayName("KIS 업종 기간별 시세(코스피 일별)를 실제 호출한다")
    void getKospiIndexPeriodPrice() throws JsonProcessingException {
        // given
        String token = "Bearer " + tokenHolder.getAccessToken();
        String appKey = tokenHolder.getAppKey();
        String appSecret = tokenHolder.getAppSecret();

        String trId = "FHKUP03500100";   // 업종 기간별 시세용 TR ID
        String custType = "P";            // 개인 고객
        String marketCode = "U";          // 업종 구분 (U)
        String indexCode = "0001";        // 코스피
        String periodDivCode = "D";       // D: 일별
        String startDate = "20251010";
        String endDate = "20251001";

        // when
        KisPeriodResponseWrapper<KisIndexCurrentPrice, KisIndexPeriodPrice> response =
                kisIndexClient.getIndexPeriodPrice(
                        "application/json; charset=utf-8",
                        token,
                        appKey,
                        appSecret,
                        trId,
                        custType,
                        marketCode,
                        indexCode,
                        startDate,
                        endDate,
                        periodDivCode
                );

        // then
        // 전체 JSON 출력
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        log.info("[Full JSON Response]\n{}", json);

        log.info("응답 코드: {}", response.getResultCode());
        log.info("응답 메시지: {}", response.getMessage());

        List<KisIndexPeriodPrice> dataList = response.getOutput2();
        if (dataList != null && !dataList.isEmpty()) {
            log.info("📊 [KOSPI 업종 기간별 시세 조회 결과]");
            for (KisIndexPeriodPrice data : dataList) {
                log.info("일자: {}, 종가: {}, 등락폭: {}, 등락률: {}%",
                        data.getBaseDate(),
                        data.getClosePrice(),
                        data.getChangeAmount(),
                        data.getChangeRate()
                );
            }
        } else {
            log.warn("⚠️ 조회된 데이터가 없습니다.");
        }

        assert response.getResultCode().equals("0") : "API 호출 실패";
    }
}