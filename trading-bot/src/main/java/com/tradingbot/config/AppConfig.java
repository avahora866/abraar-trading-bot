package com.tradingbot.config;

import net.jacobpeterson.alpaca.AlpacaAPI;
// This is the correct import path according to the 10.0.1 javadoc
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${alpaca.api.key}")
    private String traderApiKey;

    @Value("${alpaca.api.secret}")
    private String traderSecret;

    @Value("${alpaca.api.base-url}")
    private String baseUrl;

    @Bean
    public AlpacaAPI alpacaAPI() {
        return new AlpacaAPI(traderApiKey, traderSecret, TraderAPIEndpointType.PAPER, null);
    }
}