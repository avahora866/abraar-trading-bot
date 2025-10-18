package com.tradingbot.alpaca;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBarsResp;
import net.jacobpeterson.alpaca.openapi.trader.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AlpacaService {

    private static final Logger logger = LoggerFactory.getLogger(AlpacaService.class);

    private final AlpacaAPI alpacaAPI;

    @Autowired
    public AlpacaService(AlpacaAPI alpacaAPI) {
        this.alpacaAPI = alpacaAPI;
    }

    public List<StockBar> getBars(String symbol, ZonedDateTime start, ZonedDateTime end, int limit, String timeframe) {
        try {
            StockBarsResp response = alpacaAPI.marketData().stock().stockBars(symbol, timeframe, start.toOffsetDateTime(), end.toOffsetDateTime(), (long) limit, null, null, null, null, null, null);
            if (response != null && response.getBars().containsKey(symbol)) {
                return response.getBars().get(symbol);
            }
        } catch (ApiException e) {
            logger.error("Error fetching bars for {}: {}", symbol, e.getMessage());
        }
        return List.of();
    }

    public void submitMarketOrder(String symbol, BigDecimal quantity, OrderSide side) {
        try {
            PostOrderRequest postOrderRequest = new PostOrderRequest();
            postOrderRequest.setSymbol(symbol);
            postOrderRequest.setQty(String.valueOf(quantity));
            postOrderRequest.setSide(side);
            postOrderRequest.setType(OrderType.MARKET);
            postOrderRequest.setTimeInForce(TimeInForce.DAY);
            Order order = alpacaAPI.trader().orders().postOrder(postOrderRequest);
            logger.info("Submitted {} market order {} for {} {} shares.", order.getId(), side, quantity, symbol);
        } catch (net.jacobpeterson.alpaca.openapi.trader.ApiException e) {
            logger.error("Error submitting order for {}: {}", symbol, e.getMessage());
        }
    }
}