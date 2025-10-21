package com.stockport.server.stock.config;

import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class HolidayConfig {
    @Bean
    public HolidayManager holidayManager() throws Exception {
        // 리소스를 URL로 직접 지정
        URL xmlUrl = this.getClass().getClassLoader().getResource("holidays/KR.xml");
        if (xmlUrl == null) {
            throw new IllegalStateException("KR.xml holiday resource not found in classpath");
        }
        return HolidayManager.getInstance(ManagerParameters.create(xmlUrl));
    }
}