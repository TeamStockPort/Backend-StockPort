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

    @GetMapping("/save-daily-data")
    @Operation(
            summary = "일별 주식 데이터 저장",
            description = "매일 종가 기준으로 일별 주식 데이터를 저장합니다."
    )
    public ApiResponse<String> saveDailyStockData() {
        stockService.saveDailyStockData();
        return ApiResponse.onSuccess("일별 주가 데이터 저장 성공");
    }

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

    @GetMapping("/update/stock-data")
    @Operation(
            summary = "주식 데이터 강제 업데이트",
            description = "주식 데이터를 강제 업데이트합니다."
    )
    public ApiResponse<String> updateErrorStockData(@RequestParam String stockCd) {
        stockService.forceUpdateStockData(stockCd);
        return ApiResponse.onSuccess("오류 주가 데이터 재업데이트 성공");
    }

    @GetMapping("/update/all-stock-prices")
    @Operation(
            summary = "모든 주식 가격 데이터 업데이트",
            description = "모든 주식의 가격 데이터를 업데이트합니다. (오래 걸릴 수 있음)"
    )
    public ApiResponse<String> updateAllStockPriceData() {
        stockService.updateAllStockPriceData();
        return ApiResponse.onSuccess("모든 주식 가격 데이터 업데이트 성공");
    }
}
