package com.stockport.server.domain.stock.repository;

import com.stockport.server.application.controller.stock.dto.StockPriceResponse;
import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockAndBaseDateBetween(Stock stock, LocalDate startDate, LocalDate endDate);

    boolean existsByStockAndBaseDate(Stock stock, LocalDate baseDate);
}
