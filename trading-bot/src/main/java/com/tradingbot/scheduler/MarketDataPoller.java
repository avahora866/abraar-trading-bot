package com.tradingbot.scheduler;

import com.tradingbot.alpaca.AlpacaService;
import com.tradingbot.events.MarketDataEvent;
// REMOVE the TimeFrame import.
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class MarketDataPoller {
    private static final Logger logger = LoggerFactory.getLogger(MarketDataPoller.class);

    @Value("${trading.symbol}")
    private String symbol;
    
    @Value("${strategy.sma.long-window}")
    private int longWindow;

    @Autowired
    private AlpacaService alpacaService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 */15 * * * ?") // Every 15 minutes
    public void pollMarketData() {
        logger.info("Polling market data for {}", symbol);
        ZonedDateTime now = ZonedDateTime.now();
        
        // Pass the String "1Day" for the timeframe
        List<StockBar> bars = alpacaService.getBars(
                symbol, 
                now.minusDays(60), 
                now, 
                longWindow + 1, 
                "1Day" // <-- This is the fix
        );
        
        if (bars != null && !bars.isEmpty()) {
            eventPublisher.publishEvent(new MarketDataEvent(this, symbol, bars));
        }
    }
}