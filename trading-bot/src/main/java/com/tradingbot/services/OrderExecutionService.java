package com.tradingbot.services;

import com.tradingbot.alpaca.AlpacaService;
import com.tradingbot.events.SignalEvent;
// Import for the new OrderSide enum
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(OrderExecutionService.class);

    @Autowired
    private AlpacaService alpacaService;

    @Async
    @EventListener
    public void handleSignal(SignalEvent event) {
        logger.info("Executing signal {} for symbol {}", event.getSignal(), event.getSymbol());
        
        // Use BigDecimal for quantity. This should be based on a position sizing model.
        // For this example, we'll just trade one share.
        BigDecimal orderQuantity = BigDecimal.ONE; 

        switch (event.getSignal()) {
            case BUY:
                alpacaService.submitMarketOrder(event.getSymbol(), orderQuantity, OrderSide.BUY);
                break;
            case SELL:
                alpacaService.submitMarketOrder(event.getSymbol(), orderQuantity, OrderSide.SELL);
                break;
            default:
                logger.warn("Received HOLD signal, no action taken.");
                break;
        }
    }
}