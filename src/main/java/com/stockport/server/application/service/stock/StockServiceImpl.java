package com.stockport.server.application.service.stock;

import com.stockport.server.application.controller.stock.dto.StockInfoResponse;
import com.stockport.server.application.controller.stock.dto.StockPriceResponse;
import com.stockport.server.application.controller.stock.dto.StockQueryResponse;
import com.stockport.server.application.controller.stock.dto.StockRankResponse;
import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.repository.StockPriceRepository;
import com.stockport.server.domain.stock.repository.StockRepository;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;

    @Override
    public Page<StockRankResponse> getStocksByMarketCap(Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findAllByOrderByMarketCapDesc(pageable);

        AtomicInteger index = new AtomicInteger((int) pageable.getOffset() + 1);

        return stockPage.map(stock ->
                StockRankResponse.of(
                        stock,
                        index.getAndIncrement(),
                        List.of(StockPriceResponse.of(stock.getCurrentPriceInfo()))
                )
        );
    }

    @Override
    public StockInfoResponse getStockInfo(String stockCode, LocalDate startDate, LocalDate endDate) {
        Stock stock = stockRepository.findByStockCd(stockCode)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_NOT_FOUND));
        List<StockPriceResponse> stockPriceResponseList = stockPriceRepository.findByStockAndBaseDateBetween(stock, startDate, endDate)
                .stream().map(StockPriceResponse::of).toList();
        return StockInfoResponse.of(stock, stockPriceResponseList);
    }

    @Override
    public List<StockQueryResponse> searchStocks(String query) {
        List<Stock> stocks = stockRepository.findTop10ByStockNameContainingIgnoreCaseOrStockCdContainingIgnoreCaseOrIsinCdContainingIgnoreCaseOrderByStockNameAsc(query, query, query);

        return stocks.stream()
                .map(StockQueryResponse::of)
                .toList();
    }
}
