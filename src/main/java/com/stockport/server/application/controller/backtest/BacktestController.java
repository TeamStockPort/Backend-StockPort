package com.stockport.server.application.controller.backtest;

import com.stockport.server.application.controller.backtest.dto.request.BacktestRequest;
import com.stockport.server.application.controller.backtest.dto.response.BacktestResponse;
import com.stockport.server.application.service.backtest.BacktestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Backtest", description = "백테스팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backtest")
public class BacktestController {
    private final BacktestService backtestService;

    @PostMapping
    public ResponseEntity<BacktestResponse> runBacktest(@RequestBody BacktestRequest request) {
        backtestService.validateRequest(request);
        return ResponseEntity.ok(backtestService.runBacktest(request));
    }
}
