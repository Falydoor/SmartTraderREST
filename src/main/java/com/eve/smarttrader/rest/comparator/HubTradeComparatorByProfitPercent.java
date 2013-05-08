package com.eve.smarttrader.rest.comparator;

import java.util.Comparator;

import com.eve.smarttrader.rest.models.json.HubTrade;

public class HubTradeComparatorByProfitPercent implements Comparator<HubTrade> {
    @Override
    public int compare(HubTrade firstHubTrade, HubTrade secondHubTrade) {
        return Double.compare(secondHubTrade.getPercentProfit(), firstHubTrade.getPercentProfit());
    }
}
