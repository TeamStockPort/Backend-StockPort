package com.stockport.server.stock.config;

import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HolidayConfig {
    @Bean
    public HolidayManager holidayManager() {
        return HolidayManager.getInstance(ManagerParameters.create("kr"));
    }
}
