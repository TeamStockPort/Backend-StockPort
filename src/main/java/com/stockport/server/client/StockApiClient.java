package com.stockport.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockport.server.dto.StockInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StockApiClient {
    private final String baseUrl;
    private final String apiKey;
    private final RestTemplate restTemplate;
//    private final ObjectMapper om;

    public StockApiClient() {
        baseUrl = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";
        apiKey = "c49c143982d14a55271e9cb2c0c6ffa4abd0e6eb9558bccb0ba7279906567552";
        this.restTemplate = new RestTemplate();
//        this.om = new ObjectMapper();
    }

    public String fetchAllByBasDT(String basDt) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("resultType", "json")
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("basDt", basDt)
                .toUriString();
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        // 요청 엔티티 (GET이므로 바디 없음)
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // exchange로 호출
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        return response.getStatusCode() + " " + response.getBody();
    }
}
