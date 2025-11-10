package com.stockport.server.application.controller.stock;

import com.stockport.server.application.service.stock.StockService;
import com.stockport.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Stock Data Update", description = "주식 데이터 업데이트 요청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/update")
public class StockUpdateController {
    private final StockService stockService;

    @GetMapping("/update-current-data")
    @Operation(
            summary = "현재 주식 데이터 업데이트",
            description = "외부 API를 통해 현재 주식 데이터를 업데이트합니다. (약 5-10분 소요)"
    )
    public ApiResponse<String> updateCurrentStockData() {
        stockService.updateCurrentStockData();
        return ApiResponse.onSuccess("현재 주가 데이터 업데이트 성공");
    }

    @GetMapping("/update/periodic-data")
    @Operation(
            summary = "주가 데이터 기간 지정 업데이트",
            description = "입력한 기간의 주가 데이터를 업데이트 합니다. 140일 이상의 기간을 지정시 데이터 누락이 있을 수 있습니다. (약 1시간 소요)"
    )
    public ApiResponse<String> updatePeriodicStockData(
            @Parameter(description = "조회 시작 날짜 (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam LocalDate startDate,
            @Parameter(description = "조회 종료 날짜 (yyyy-MM-dd)", example = "2024-06-01")
            @RequestParam LocalDate endDate
    ) {
        stockService.updatePeriodicStockData(startDate, endDate);
        return ApiResponse.onSuccess("주가 데이터 업데이트 성공");
    }

    @GetMapping("/update/historical-data")
    @Operation(
            summary = "과거 주식 데이터 전체 업데이트",
            description = "과거 주식 데이터를 처음부터 현재까지 모두 업데이트 합니다. (1일 이상 소요)"
    )
    public ApiResponse<String> updateHistoricalStockData() {
        stockService.updateHistoricalStockData();
        return ApiResponse.onSuccess("과거 주가 데이터 전체 업데이트 성공");
    }
}
