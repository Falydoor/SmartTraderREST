package com.eve.smarttrader.rest.models.json;

import com.google.gson.annotations.Expose;

public class StationTrade extends Trade {

    @Expose
    private long buyPrice;

    @Expose
    private long percentProfit;

    @Expose
    private long profit;

    public long getBuyPrice() {
        return buyPrice;
    }

    public long getPercentProfit() {
        return percentProfit;
    }

    public long getProfit() {
        return profit;
    }

    public void setBuyPrice(long buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setPercentProfit(long percentProfit) {
        this.percentProfit = percentProfit;
    }

    public void setProfit(long profit) {
        this.profit = profit;
    }

}
