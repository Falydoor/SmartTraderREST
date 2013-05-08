package com.eve.smarttrader.rest.comparator;

import java.util.Comparator;

import com.eve.smarttrader.rest.models.jpa.MarketOrder;

public class MarketOrderComparatorByBidAndPrice implements Comparator<MarketOrder> {
    @Override
    public int compare(MarketOrder mo1, MarketOrder mo2) {
        int bidComp = Integer.valueOf(mo1.isBid() ? 1 : 0).compareTo(mo2.isBid() ? 1 : 0);
        if (bidComp == 0) {
            return Long.valueOf(mo1.getPrice()).compareTo(mo2.getPrice());
        } else {
            return bidComp;
        }
    }
}
