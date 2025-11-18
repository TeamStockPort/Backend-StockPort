package com.stockport.server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI stockPortOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StockPort API 명세서")
                        .description("주식 데이터 조회 및 백테스팅 시스템 API 문서")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}