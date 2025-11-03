package com.stockport.server.domain.stock.repository;

import com.stockport.server.domain.stock.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Page<Stock> findAllByOrderByMarketCapDesc(Pageable pageable);
    Optional<Stock> findByStockCd(String stockCd);
    List<Stock> findTop10ByStockNameContainingIgnoreCaseOrStockCdContainingIgnoreCaseOrIsinCdContainingIgnoreCase(String query, String query2, String query3);
}
