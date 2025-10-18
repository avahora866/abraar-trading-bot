package com.tradingbot.services;

import com.tradingbot.events.MarketDataEvent;
import com.tradingbot.events.SignalEvent;
import com.tradingbot.strategy.Signal;
import com.tradingbot.strategy.TradingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignalGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(SignalGeneratorService.class);

    @Autowired
    private List<TradingStrategy> strategies; 

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void handleMarketData(MarketDataEvent event) {
        logger.info("Processing market data for {} with {} strategies.", event.getSymbol(), strategies.size());
        for (TradingStrategy strategy : strategies) {
            Signal signal = strategy.generateSignal(event.getBars());
            if (signal != Signal.HOLD) {
                logger.info("Strategy '{}' generated signal: {} for {}", strategy.getName(), signal, event.getSymbol());
                eventPublisher.publishEvent(new SignalEvent(this, event.getSymbol(), signal));
            }
        }
    }
}