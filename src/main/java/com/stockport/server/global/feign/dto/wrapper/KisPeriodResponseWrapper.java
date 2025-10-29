package com.stockport.server.global.feign.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KisPeriodResponseWrapper<T, S> {
    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String message;

    @JsonProperty("output1")
    private T output1;

    @JsonProperty("output2")
    private List<S> output2;
}
