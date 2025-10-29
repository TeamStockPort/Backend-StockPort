package com.stockport.server.application.controller.IndexData;

import com.stockport.server.application.controller.IndexData.dto.CurrentIndexResponse;
import com.stockport.server.application.controller.IndexData.dto.PeriodIndexResponse;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.application.service.indexData.IndexDataService;
import com.stockport.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Index Data", description = "지수 데이터 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index")
public class IndexDataController {
    private final IndexDataService indexDataService;

    @Operation(
            summary = "현재 지수 데이터 조회",
            description = "코스피와 코스닥의 현재 지수 데이터를 조회합니다."
    )
    @GetMapping("/current")
    public ApiResponse<CurrentIndexResponse> getCurrentIndexData() {
        var kospi = indexDataService.getCurrentIndexData(MarketType.KOSPI);
        var kosdaq = indexDataService.getCurrentIndexData(MarketType.KOSDAQ);

        return ApiResponse.onSuccess(CurrentIndexResponse.of(kospi, kosdaq));
    }

    @Operation(
            summary = "기간별 지수 데이터 조회",
            description = "코스피와 코스닥의 기간별 지수 데이터를 조회합니다."
    )
    @GetMapping("/period/{marketType}")
    public ApiResponse<PeriodIndexResponse> getPeriodIndexData(@PathVariable MarketType marketType, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        var data = indexDataService.getPeriodIndexData(marketType, startDate, endDate);

        var response = PeriodIndexResponse.builder()
                .marketType(marketType)
                .startDate(startDate)
                .endDate(endDate)
                .data(data)
                .build();

        return ApiResponse.onSuccess(response);
    }

    @Operation(
            summary = "과거 지수 데이터 업데이트",
            description = "코스피와 코스닥의 10년치 지수 데이터를 업데이트해서 저장합니다."
    )
    @GetMapping("/update/historical")
    public ApiResponse<String> updateHistoricalIndexData() {
        indexDataService.updateHistoricalIndexData(MarketType.KOSPI);
        indexDataService.updateHistoricalIndexData(MarketType.KOSDAQ);
        return ApiResponse.onSuccess("Historical index data update initiated.");
    }
}
