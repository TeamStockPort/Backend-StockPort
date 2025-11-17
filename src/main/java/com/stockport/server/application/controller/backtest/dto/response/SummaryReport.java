package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SummaryReport {

    private String portfolioName;
    private long initialCapital;
    private long finalCapital;

    private BigDecimal annualReturn;
    private BigDecimal maxDrawdown;
    private BigDecimal volatility;
    private BigDecimal sharpeRatio;
    private BigDecimal sortinoRatio;

    private BigDecimal bestYearReturn;
    private BigDecimal worstYearReturn;
}