package com.eve.smarttrader.rest.comparator;

import java.util.Comparator;

import com.eve.smarttrader.rest.models.json.StationTrade;

public class StationTradeComparatorByProfit implements Comparator<StationTrade> {
    @Override
    public int compare(StationTrade firstHubTrade, StationTrade secondHubTrade) {
        return Double.compare(secondHubTrade.getProfit(), firstHubTrade.getProfit());
    }
}
