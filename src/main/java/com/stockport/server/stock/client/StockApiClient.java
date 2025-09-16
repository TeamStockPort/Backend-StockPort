package com.stockport.server.stock.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockport.server.stock.dto.StockInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockApiClient {
    @Value("${api.url}")
    private String baseUrl;
    @Value("${api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public StockApiClient() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public List<StockInfoResponse> fetchAllByBasDT(String basDt) {
        String url = buildUrl(basDt, 1, 10000);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new IllegalStateException("API 요청 실패: " + response.getStatusCode());
        }

        try {
            String responseBody = response.getBody();
            var root = mapper.readTree(responseBody);
            var header = root.path("response").path("header");
            var resultCode = header.path("resultCode").asText("");
            if (!"00".equals(resultCode)) {
                var msg = header.path("resultMsg").asText("");
                throw new IllegalStateException("API 오류: " + resultCode + " / " + msg);
            }

            var itemsNode = root.path("response").path("body").path("items").path("item");

            // item이 배열로 오거나(다건), 객체 하나로 오거나(단건) 모두 대응
            if (itemsNode.isArray()) {
                return mapper.readerForListOf(StockInfoResponse.class).readValue(itemsNode);
            } else if (!itemsNode.isMissingNode() && !itemsNode.isNull()) {
                var one = mapper.treeToValue(itemsNode, StockInfoResponse.class);
                return java.util.List.of(one);
            } else {
                return java.util.Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException("상장종목 응답 파싱 실패", e);
        }
    }

    private String buildUrl(String basDt, int pageNo, int numOfRows) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("resultType", "json")
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("basDt", basDt)
                .toUriString();
    }
}
