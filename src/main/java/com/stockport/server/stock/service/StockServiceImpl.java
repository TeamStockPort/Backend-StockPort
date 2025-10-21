package com.stockport.server.stock.service;

import com.stockport.server.stock.client.StockApiClient;
import com.stockport.server.stock.domain.Stock;
import com.stockport.server.stock.dto.StockInfoDto;
import com.stockport.server.stock.repository.StockRepository;
import de.focus_shift.jollyday.core.HolidayManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockApiClient stockApiClient;

    @Override
    public void fetchAndStoreStocks() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        String baseDt = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        var stockInfos = stockApiClient.fetchAllByBasDT(baseDt);
        if (stockInfos.isEmpty()) { // 데이터가 없는 경우 처리
            log.info("No stock data fetched for date: " + baseDt);
            return;
        }

        var stocks = stockInfos.stream()
                .map(StockInfoDto::toEntity)
                .toList();
        // 새로 조회한 ISIN 코드 집합
        var incomingIsins = stocks.stream()
                .map(Stock::getIsinCd)
                .filter(Objects::nonNull)
                .collect(toSet());
        // 현재 DB에 존재하는 ISIN 코드 목록
        var existingIsins = new HashSet<>(stockRepository.findAll()
                .stream()
                .map(Stock::getIsinCd)
                .toList());
        // DB에는 있는데 새 조회 결과에 없는 종목 삭제
        existingIsins.removeAll(incomingIsins);
        if (!existingIsins.isEmpty()) {
            stockRepository.deleteAllById(existingIsins);
        }
        // 새 데이터 저장 또는 업데이트
        stockRepository.saveAll(stocks);
    }
}
