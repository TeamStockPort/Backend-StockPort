package com.stockport.server.stock.client;

import com.stockport.server.stock.dto.StockPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceApiClient {
    @Value("${api.key}")
    private String apiKey;
    private String url = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService";
    private final RestTemplate restTemplate = new RestTemplate();

    // 모든 주식의 가격 정보를 날짜(basDt)로 조회
    public StockPriceDto[] getAllStockPricesByDate(String basDt) {
        String endpoint = url + "/getStockPriceInfo";
        String uri = UriComponentsBuilder.fromHttpUrl(endpoint)
                .queryParam("serviceKey", apiKey)
                .queryParam("resultType", "json")
                .queryParam("basDt", basDt)
                .queryParam("numOfRows", "10000")
                .queryParam("pageNo", "1")
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("API 요청 실패: " + response.getStatusCode());
        }

        try {
            String responseBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode header = root.path("response").path("header");
            String resultCode = header.path("resultCode").asText("");
            if (!"00".equals(resultCode)) {
                String msg = header.path("resultMsg").asText("");
                throw new IllegalStateException("API 오류: " + resultCode + " / " + msg);
            }

            JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                List<StockPriceDto> list = mapper.readerForListOf(StockPriceDto.class).readValue(itemsNode);
                return list.toArray(new StockPriceDto[0]);
            } else if (!itemsNode.isMissingNode() && !itemsNode.isNull()) {
                StockPriceDto one = mapper.treeToValue(itemsNode, StockPriceDto.class);
                return new StockPriceDto[]{ one };
            } else {
                return new StockPriceDto[0];
            }
        } catch (Exception e) {
            HttpStatusCode st = response.getStatusCode();
            MediaType ct = response.getHeaders().getContentType();
            String body = response.getBody();
            String peek = (body == null) ? "null" : body.substring(0, Math.min(body.length(), 500));
            log.error("파싱 실패 - status={}, contentType={}, body[0..500]={}", st, ct, peek, e);
            throw new RuntimeException("주식시세 응답 파싱 실패", e);
        }
    }

    // 주식코드로 주식의 여태까지의 모든 가격정보를 조회
    public StockPriceDto[] getStockPriceHistory(String stockCode) {
        String endpoint = url + "/getStockPriceInfo";
        String uri = UriComponentsBuilder.fromHttpUrl(endpoint)
                .queryParam("serviceKey", apiKey)
                .queryParam("resultType", "json")
                .queryParam("isinCd", stockCode)
                .queryParam("numOfRows", "10000")
                .queryParam("pageNo", "1")
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("API 요청 실패: " + response.getStatusCode());
        }

        try {
            String responseBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode header = root.path("response").path("header");
            String resultCode = header.path("resultCode").asText("");
            if (!"00".equals(resultCode)) {
                String msg = header.path("resultMsg").asText("");
                throw new IllegalStateException("API 오류: " + resultCode + " / " + msg);
            }

            JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                List<StockPriceDto> list = mapper.readerForListOf(StockPriceDto.class).readValue(itemsNode);
                return list.toArray(new StockPriceDto[0]);
            } else if (!itemsNode.isMissingNode() && !itemsNode.isNull()) {
                StockPriceDto one = mapper.treeToValue(itemsNode, StockPriceDto.class);
                return new StockPriceDto[]{ one };
            } else {
                return new StockPriceDto[0];
            }
        } catch (Exception e) {
            HttpStatusCode st = response.getStatusCode();
            MediaType ct = response.getHeaders().getContentType();
            String body = response.getBody();
            String peek = (body == null) ? "null" : body.substring(0, Math.min(body.length(), 500));
            log.error("파싱 실패 - status={}, contentType={}, body[0..500]={}", st, ct, peek, e);
            throw new RuntimeException("주식시세(개별) 응답 파싱 실패", e);
        }
    }
}
