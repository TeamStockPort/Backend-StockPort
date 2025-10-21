package com.stockport.server.domain.stock.repository;

import com.stockport.server.domain.stock.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
