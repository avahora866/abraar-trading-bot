/*
 * $HeadURL$
 *
 * (c) 2025 IIZUKA Software Technologies Ltd.  All rights reserved.
 */
package org.example.app.utility;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.broker.ApiException;
import net.jacobpeterson.alpaca.openapi.broker.model.Asset;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBarsRespSingle;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderType;
import net.jacobpeterson.alpaca.openapi.trader.model.Position;
import net.jacobpeterson.alpaca.openapi.trader.model.PostOrderRequest;
import net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce;

/**
 *
 *
 * @author Abraar Vahora
 * @version $Id$
 */
public class AlpacaClient
{
	private final AlpacaAPI alpacaAPI;
	
	public AlpacaClient(AlpacaAPI alpacaAPI)
	{
		this.alpacaAPI = alpacaAPI;
	}
	
	
	public List<Asset> getAssets(String status, String assetType, List<String> attributes)
	{
		AuditManager.event("Retrieving assets");
		try
		{
			List<Asset> assets = alpacaAPI.broker().assets().getAssets(status, assetType, attributes);
			AuditManager.event("Retrieved assets");
			return assets;
		}
		catch (ApiException e)
		{
			throw new RuntimeException("Could not retrieve assets");
		}
	}
	
	public StockBarsRespSingle stockBarSingle(String symbol)
	{
		AuditManager.event("Retrieving stockBarSingle");
		
		OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
		OffsetDateTime start = end.minusDays(30); // fetch enough bars to get 20 daily
		
		try
		{
			StockBarsRespSingle stockBarsRespSingle = this.alpacaAPI.marketData()
				.stock()
				.stockBarSingle(symbol, "day", start, end, null, null, null, null, null, null, null);
			AuditManager.event("Retrieved stockBarSingle");
			return stockBarsRespSingle;
		}
		catch (net.jacobpeterson.alpaca.openapi.marketdata.ApiException e)
		{
			throw new RuntimeException("Could not retrieve stockBarsRespSingle");
		}
		
	}
	
	public void sellAllPositions()
	{
		try
		{
			// 1. Get all current positions
			List<Position> positions = alpacaAPI.trader().positions().getAllOpenPositions();
			
			for (Position position : positions)
			{
				String symbol = position.getSymbol();
				int qty = Integer.parseInt(position.getQty()); // quantity as int
				
				if (qty > 0)
				{
					Order order = sell(symbol, qty);
					
					System.out.println("Order submitted: " + order.getId());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private Order sell(String symbol, int qty) throws net.jacobpeterson.alpaca.openapi.trader.ApiException
	{
		System.out.println("Selling " + qty + " shares of " + symbol);
		PostOrderRequest postOrderRequest = new PostOrderRequest();
		postOrderRequest.setSymbol(symbol);
		postOrderRequest.setQty(String.valueOf(qty));
		postOrderRequest.setSide(OrderSide.SELL);
		postOrderRequest.setType(OrderType.MARKET);
		postOrderRequest.setTimeInForce(TimeInForce.DAY);
		Order order = alpacaAPI.trader().orders().postOrder(postOrderRequest);
		AuditManager.sell(symbol, qty, order.getStopPrice());
		return order;
	}
}
