package com.stockport.server.stock.repository;

import com.stockport.server.stock.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
