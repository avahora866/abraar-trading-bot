/*
 * $HeadURL$
 * 
 * (c) 2025 IIZUKA Software Technologies Ltd.  All rights reserved.
 */
package org.example.app.utility;

import java.util.List;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBarsRespSingle;

/**
 *
 *
 * @author Abraar Vahora
 * @version $Id$
 */
public class StockFilter
{
	private final AlpacaAPI alpaca;
	
	public StockFilter(AlpacaAPI alpaca) {
		this.alpaca = alpaca;
	}
	
	public boolean isTrending(StockBarsRespSingle stockBarsRespSingle) {
		// Fetch historical bars (e.g., 20-day)
		List<StockBar> bars = stockBarsRespSingle.getBars();
		if (bars.size() < 20) return false;
		
		double sum = 0;
		for (StockBar bar : bars) {
			sum += bar.getC();
		}
		double avg = sum / bars.size();
		
		double latest = bars.get(bars.size() - 1).getC();
		
		// Simple trending check: price consistently above or below average
		return latest > avg; // uptrend; reverse for downtrend
	}
	
	public double calculateVolatility(StockBarsRespSingle stockBarsRespSingle) {
		double sum = 0, mean;
		List<StockBar> bars = stockBarsRespSingle.getBars();
		for (StockBar bar : bars) sum += bar.getC();
		mean = sum / bars.size();
		
		double variance = 0;
		for (StockBar bar : bars) variance += Math.pow(bar.getC() - mean, 2);
		variance /= bars.size();
		
		return Math.sqrt(variance); // standard deviation as volatility
	}
	
	public double averageVolume(StockBarsRespSingle stockBarsRespSingle) {
		return stockBarsRespSingle.getBars().stream().mapToDouble(StockBar::getV).average().orElse(0);
	}
	
}
