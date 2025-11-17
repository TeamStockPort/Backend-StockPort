package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PortfolioValue {
    private LocalDate date;
    private BigDecimal value;

    public static PortfolioValue create(LocalDate date, BigDecimal returnRate) {
        return PortfolioValue.builder()
                .date(date)
                .value(returnRate)
                .build();
    }
}
