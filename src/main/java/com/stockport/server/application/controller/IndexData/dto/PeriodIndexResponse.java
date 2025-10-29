package com.stockport.server.application.controller.IndexData.dto;

import com.stockport.server.domain.indexData.constant.MarketType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PeriodIndexResponse {
    private MarketType marketType;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<IndexDataResponse> data;
}
