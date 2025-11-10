package com.stockport.server.application.service.stock;

import com.stockport.server.application.controller.stock.dto.StockInfoResponse;
import com.stockport.server.application.controller.stock.dto.StockQueryResponse;
import com.stockport.server.application.controller.stock.dto.StockRankResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StockService {
    Page<StockRankResponse> getStocksByMarketCap(Pageable pageable);

    StockInfoResponse getStockInfo(String stockCode, LocalDate startDate, LocalDate endDate);

    List<StockQueryResponse> searchStocks(String query);

    void updateCurrentStockData();

    void updatePeriodicStockData(LocalDate startDate, LocalDate endDate);

    void updateHistoricalStockData();
}
