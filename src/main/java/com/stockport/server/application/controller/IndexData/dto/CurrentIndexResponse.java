package com.stockport.server.application.controller.IndexData.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentIndexResponse {
    private IndexDataResponse kospi;
    private IndexDataResponse kosdaq;

    public static CurrentIndexResponse of(IndexDataResponse kospi, IndexDataResponse kosdaq) {
        return CurrentIndexResponse.builder()
                .kospi(kospi)
                .kosdaq(kosdaq)
                .build();
    }
}
