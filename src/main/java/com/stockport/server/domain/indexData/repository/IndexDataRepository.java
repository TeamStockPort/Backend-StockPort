package com.stockport.server.domain.indexData.repository;

import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {
    Optional<IndexData> findByMarketTypeAndBaseDate(MarketType marketType, LocalDate now);
    Optional<IndexData> findTopByMarketTypeOrderByBaseDateDesc(MarketType marketType);

    List<IndexData> findAllByMarketTypeAndBaseDateBetweenOrderByBaseDateAsc(MarketType marketType, LocalDate startDate, LocalDate endDate);

    boolean existsByMarketTypeAndBaseDate(MarketType marketType, LocalDate baseDate);
}
