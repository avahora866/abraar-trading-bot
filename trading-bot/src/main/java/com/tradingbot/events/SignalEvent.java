package com.tradingbot.events;

import com.tradingbot.strategy.Signal;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SignalEvent extends ApplicationEvent {
    private final String symbol;
    private final Signal signal;

    public SignalEvent(Object source, String symbol, Signal signal) {
        super(source);
        this.symbol = symbol;
        this.signal = signal;
    }
}