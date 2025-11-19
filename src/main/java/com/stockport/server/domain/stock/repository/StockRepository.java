package com.stockport.server.domain.stock.repository;

import com.stockport.server.domain.stock.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Page<Stock> findAllByOrderByMarketCapDesc(Pageable pageable);
    List<Stock> findAllByOrderByMarketCapDesc();
    Optional<Stock> findByStockCd(String stockCd);
    List<Stock> findTop10ByStockNameContainingIgnoreCaseOrStockCdContainingIgnoreCaseOrIsinCdContainingIgnoreCaseOrderByMarketCapDesc(
            String name, String code, String isin
    );
}
