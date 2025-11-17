package com.stockport.server.application.controller.backtest.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BacktestRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long initialCapital;
    private RebalanceCycle rebalanceCycle;
    private List<AssetRequest> assets;
}