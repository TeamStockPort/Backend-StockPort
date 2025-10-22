package com.stockport.server.domain.stock.repository;

import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockCurrentPriceRepository extends JpaRepository<StockCurrentPrice, Long> {
}
