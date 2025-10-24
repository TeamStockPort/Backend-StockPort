package com.stockport.server.global.feign.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}