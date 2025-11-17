package com.stockport.server.domain.stock.repository;

import com.stockport.server.application.controller.stock.dto.StockPriceResponse;
import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockAndBaseDateBetweenOrderByBaseDateDesc(Stock stock, LocalDate startDate, LocalDate endDate);

    @Query("""
    select sp.baseDate
    from StockPrice sp
    where sp.stock = :stock
      and sp.baseDate between :start and :end
""")
    List<LocalDate> findAllBaseDatesByStockAndDateRange(
            @Param("stock") Stock stock,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<StockPrice> findAllByBaseDate(LocalDate today);
}
