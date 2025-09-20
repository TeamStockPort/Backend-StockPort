package com.stockport.server.stock.scheduler;

import com.stockport.server.stock.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceScheduler {
    private final StockPriceService stockPriceService;

    @Scheduled(cron = "0 50 23 * * *", zone = "Asia/Seoul") // 매일 오후 11시 50분에 실행
    public void runDaily() {
        try {
            stockPriceService.fetchAndSaveStockPricesByBasDt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            log.info("주식 시세 데이터 업데이트 완료");
        } catch (Exception e) {
            log.error("주식 시세 데이터 업데이트 실패: " + e.getMessage());
        }
    }
}
