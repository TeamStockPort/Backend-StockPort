package com.stockport.server.client;

import com.stockport.server.stock.client.StockApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockApiClientTest {

    @Autowired
    private StockApiClient stockApiClient;

    @Test
    void testFetchAllByBasDT() {
        // 테스트용 날짜 (문서에 나온 형식은 YYYYMMDD)
        String testDate = "20250915";

        String s = stockApiClient.fetchAllByBasDT(testDate);
        System.out.println(s);
    }
}