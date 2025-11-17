package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PortfolioReturn {
    private LocalDate date;
    private BigDecimal returnRate;

    public static PortfolioReturn create(LocalDate date, BigDecimal returnRate) {
        return PortfolioReturn.builder()
                .date(date)
                .returnRate(returnRate)
                .build();
    }
}
