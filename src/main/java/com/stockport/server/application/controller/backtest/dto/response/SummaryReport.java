package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class SummaryReport {

    private String portfolioName;
    private BigDecimal initialCapital;
    private BigDecimal finalCapital;

    private BigDecimal cagr;
    private BigDecimal maxDrawdown;
    private BigDecimal volatility;
    private BigDecimal sharpeRatio;
    private BigDecimal sortinoRatio;
}