package com.stockport.server.application.scheduler.stock;

import com.stockport.server.application.service.stock.StockService;
import com.stockport.server.domain.indexData.constant.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {
    private final StockService stockService;

    @Scheduled(cron = "0 0 23 * * MON-FRI", zone = "Asia/Seoul") // todo: 공휴일/휴장일 스케쥴러 처리 필요.
    public void updateStockData() {
        stockService.updateCurrentStockData();
        log.info("[Scheduler] 주가 데이터 업데이트 완료");
    }
}
