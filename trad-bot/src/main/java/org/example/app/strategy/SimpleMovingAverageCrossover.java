/*
 * $HeadURL$
 * 
 * (c) 2025 IIZUKA Software Technologies Ltd.  All rights reserved.
 */
package org.example.app.strategy;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.example.app.utility.AuditManager;
import org.example.app.utility.MarketCloseScheduler;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.broker.model.Asset;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBarsRespSingle;

/**
 *
 *
 * @author Abraar Vahora
 * @version $Id$
 */
public class SimpleMovingAverageCrossover extends AbstractStrategy
{
	public SimpleMovingAverageCrossover(String riskLevel, AlpacaAPI alpacaAPI)
	{
		super(riskLevel, alpacaAPI);
	}
	
	@Override
	public void commence()
	{
		List<Asset> assets = alpacaClient.getAssets("active", "us_equity", Collections.emptyList());
		
		double minVolume = 500_000; 
		double thresholdVolatility = 0.02; 
		
		this.assetsList = assets.stream().filter(asset -> {
			String symbol = asset.getSymbol();
			StockBarsRespSingle stockBarsRespSingle = null;
			stockBarsRespSingle = alpacaClient.stockBarSingle(symbol);
			return stockFilter.isTrending(stockBarsRespSingle) && 
				stockFilter.calculateVolatility(stockBarsRespSingle) > thresholdVolatility && 
				stockFilter.averageVolume(stockBarsRespSingle) > minVolume;
		}).collect(Collectors.toList());
		
		
		// Find stocks to trade
		// go through each of the filtered stocks and calculate the closing price of the last n days and store it somewhere
		// after every close go through each of the filtered stocks and decide on weather to buy or sell it
		MarketCloseScheduler marketCloseScheduler = new MarketCloseScheduler();
		for (Asset asset : this.assetsList)
		{
			StockBarsRespSingle stockBarsRespSingle = alpacaClient.stockBarSingle(asset.getSymbol());
			
			Runnable task = () -> {
				double shortSMA = stockInformation.SMAofLastN(stockBarsRespSingle, 10);
				double longSMA = stockInformation.SMAofLastN(stockBarsRespSingle, 50);
				double previousShortSMA = stockInformation.previousSMAofLastN(stockBarsRespSingle, 10);
				double previousLongSMA = stockInformation.previousSMAofLastN(stockBarsRespSingle, 50);
				if (shortSMA > longSMA && previousShortSMA <= previousLongSMA)
				{
					AuditManager.buy(asset.getSymbol(), 1, asset.getLastPrice());
				}
				else if (shortSMA < longSMA && previousShortSMA >= previousLongSMA)
				{
					// Sell signal
					AuditManager.buy(asset.getSymbol(), 1, asset.getLastPrice());
				}
			};
			
			marketCloseScheduler.scheduleAtMarketClose(task);
		}
		
		// Prevent the program from exiting
		System.out.println("Trading bot running. Type QUIT to sell all and exit.");
		
		// Block until user presses Enter
		Scanner scanner = new Scanner(System.in);

		outer:
		while (true)
		{
			String userInput = scanner.nextLine();
			if (userInput.equals("QUIT"))
			{
				marketCloseScheduler.clear();
				// Execute sell-all logic
				alpacaClient.sellAllPositions();
				break outer;
			}
		}
		
		System.out.println("Exiting program...");

	}
	
	
}
