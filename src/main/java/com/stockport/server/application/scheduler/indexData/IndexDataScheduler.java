package com.stockport.server.application.scheduler.indexData;

import com.stockport.server.application.service.indexData.IndexDataService;
import com.stockport.server.domain.indexData.constant.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexDataScheduler {
    private final IndexDataService indexDataService;

    @Scheduled(cron = "0 0/5 9-18 * * MON-FRI", zone = "Asia/Seoul") // todo: 공휴일/휴장일 스케쥴러 처리 필요.
    public void updateKospi() {
        indexDataService.updateCurrentIndexData(MarketType.KOSPI);
        log.info("[Scheduler] 코스피 데이터 업데이트 완료");
    }

    @Scheduled(cron = "30 2/5 9-18 * * MON-FRI", zone = "Asia/Seoul") // todo: 공휴일/휴장일 스케쥴러 처리 필요.
    public void updateKosdaq() {
        indexDataService.updateCurrentIndexData(MarketType.KOSPI);
        log.info("[Scheduler] 코스닥 데이터 업데이트 완료");
    }
}
