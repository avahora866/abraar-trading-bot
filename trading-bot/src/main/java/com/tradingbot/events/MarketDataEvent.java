package com.tradingbot.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
// Import for the new StockBar location
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import java.util.List;

@Getter
public class MarketDataEvent extends ApplicationEvent {
    private final String symbol;
    private final List<StockBar> bars;

    public MarketDataEvent(Object source, String symbol, List<StockBar> bars) {
        super(source);
        this.symbol = symbol;
        this.bars = bars;
    }
}