package com.stockport.server.stock.scheduler;

import com.stockport.server.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {
    private final StockService stockService;

    @Scheduled(cron = "0 50 23 * * *", zone = "Asia/Seoul") // 매일 오후 11시 50분에 실행
    public void runDaily() {
        try {
            LocalDateTime now = LocalDateTime.now();
            stockService.fetchAndStoreStocks();
            log.info("주식 데이터 업데이트 완료 " + LocalDateTime.now());
        } catch (Exception e) {
            log.error("주식 데이터 업데이트 실패: " + e.getMessage());
        }
    }
}
