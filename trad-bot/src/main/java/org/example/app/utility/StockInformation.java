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
public class StockInformation
{
	private final AlpacaAPI alpacaAPI;
	
	public StockInformation(AlpacaAPI alpaca)
	{
		this.alpacaAPI = alpaca;
	}
	
	public double SMAofLastN(StockBarsRespSingle stockBarsRespSingle, int n)
	{
		return smaCalculation(stockBarsRespSingle, n, 0);
	}
	
	public double previousSMAofLastN(StockBarsRespSingle stockBarsRespSingle, int n)
	{
		return smaCalculation(stockBarsRespSingle, n, 1);
	}
	
	private static double smaCalculation(StockBarsRespSingle stockBarsRespSingle, int n, int index)
	{
		if (stockBarsRespSingle == null || stockBarsRespSingle.getBars().isEmpty())
		{
			return 0;
		}
		
		List<StockBar> bars = stockBarsRespSingle.getBars();
		int count = Math.min(n, bars.size()); // use max 50 bars
		
		double sum = 0;
		for (int i = index; i < count; i++)
		{
			sum += bars.get(i).getC();
		}
		
		return sum / count;
	}
}
