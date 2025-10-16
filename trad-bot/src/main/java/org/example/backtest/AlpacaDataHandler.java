package org.example.backtest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBarsResp;


public class AlpacaDataHandler implements DataHandler
{
	
	private final String symbol;
	private final Iterator<StockBar> barIterator;
	
	public AlpacaDataHandler(AlpacaAPI alpacaAPI, String symbol, String startDate, String endDate)
	{
		this.symbol = symbol;
		
		ZonedDateTime start = ZonedDateTime.parse(startDate + "T00:00:00Z").withZoneSameInstant(ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.parse(endDate + "T23:59:59Z").withZoneSameInstant(ZoneOffset.UTC);
		
		try
		{
			// Note: The free Alpaca Market Data API provides IEX data.
			// For full NBBO data, you need a subscription.
			// This is the correct method for alpaca-java v10.0.1
			StockBarsResp stockBarsResp = alpacaAPI.marketData().stock().stockBars(
				symbol,
				"1Day",
				start.toOffsetDateTime(),
				end.toOffsetDateTime(),
				null, // limit - null for default
				null, // after
				null, // until
				null, null, null, null
			);
			
			Map<String, List<StockBar>> bars = stockBarsResp.getBars();
			
			if (bars == null || !bars.containsKey(symbol) || bars.get(symbol).isEmpty())
			{
				throw new IllegalStateException("No bar data returned from Alpaca for symbol: " + symbol);
			}
			
			this.barIterator = bars.get(symbol).iterator();
			System.out.println("Successfully fetched " + bars.get(symbol).size() + " bars for " + symbol);
			
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to fetch historical data from Alpaca.", e);
		}
	}
	
	@Override
	public boolean hasMoreEvents()
	{
		return barIterator.hasNext();
	}
	
	@Override
	public MarketEvent getNextEvent()
	{
		StockBar nextBar = barIterator.next();
		return new MarketEvent(symbol, nextBar);
	}
}

