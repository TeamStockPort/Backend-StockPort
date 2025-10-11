package com.stockport.server.stock.repository;

import com.stockport.server.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findByIsinCd(String isinCd);
}
