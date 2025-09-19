package com.stockport.server.stock.service;

import com.stockport.server.stock.client.StockApiClient;
import com.stockport.server.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockApiClient stockApiClient;

    @Override
    public void fetchAndStoreStocks() {
        String baseDt = LocalDate
                .now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        var stockInfos = stockApiClient.fetchAllByBasDT(baseDt);

        var stocks = stockInfos.stream()
                .map(dto -> dto.toEntity(dto))
                .toList();
        // 새로 조회한 ISIN 코드 집합
        var incomingIsins = stocks.stream()
                .map(stock -> stock.getIsinCd())
                .filter(code -> code != null)
                .collect(toSet());

        // 현재 DB에 존재하는 ISIN 코드 목록
        var existingIsins = new HashSet<>(stockRepository.findAll()
                .stream()
                .map(entity -> entity.getIsinCd())
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
