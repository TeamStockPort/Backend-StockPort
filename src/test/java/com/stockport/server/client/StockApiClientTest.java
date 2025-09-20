package com.stockport.server.client;

import com.stockport.server.stock.client.StockApiClient;
import com.stockport.server.stock.dto.StockInfoDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class StockApiClientTest {

    @Autowired
    private StockApiClient stockApiClient;

    @Test
    void testFetchAllByBasDT() {
        // 테스트용 날짜 (문서에 나온 형식은 YYYYMMDD)
        String testDate = "20250915";

        List<StockInfoDto> stockInfoRespons = stockApiClient.fetchAllByBasDT(testDate);
        Assertions.assertThat(stockInfoRespons).isNotEmpty();
    }
}