package com.eve.smarttrader.rest.models.json;

import com.eve.smarttrader.rest.models.jpa.MarketOrder;
import com.google.gson.annotations.Expose;

public class HubTrade extends Trade {

    @Expose
    private long averagePrice;
    @Expose
    private int buyerHoursOld;
    private MarketOrder buyMarketOrder;
    @Expose
    private long percentProfit;
    @Expose
    private long profit;
    private MarketOrder sellMarketOrder;
    @Expose
    private long totalPrice;
    @Expose
    private long totalProfit;
    @Expose
    private long totalQuantity;
    @Expose
    private long totalVolume;

    public void addTotalPrice(long price) {
        this.totalPrice += price;
    }

    public void addTotalQuantity(long quantity) {
        this.totalQuantity += quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != HubTrade.class) {
            return false;
        }
        HubTrade o = (HubTrade) obj;
        return this.typeID == o.getTypeID() && this.station == o.getStation();
    }

    public long getAveragePrice() {
        return this.averagePrice;
    }

    public int getBuyerHoursOld() {
        return buyerHoursOld;
    }

    public MarketOrder getBuyMarketOrder() {
        return this.buyMarketOrder;
    }

    public long getPercentProfit() {
        return this.percentProfit;
    }

    public long getProfit() {
        return this.profit;
    }

    public MarketOrder getSellMarketOrder() {
        return this.sellMarketOrder;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public long getTotalProfit() {
        return this.totalProfit;
    }

    public long getTotalQuantity() {
        return this.totalQuantity;
    }

    public long getTotalVolume() {
        return this.totalVolume;
    }

    @Override
    public int hashCode() {
        return (int) this.typeID;
    }

    public void setAveragePrice(long averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void setBuyerHoursOld(int buyerHoursOld) {
        this.buyerHoursOld = buyerHoursOld;
    }

    public void setBuyMarketOrder(MarketOrder buyMarketOrder) {
        this.buyMarketOrder = buyMarketOrder;
    }

    public void setPercentProfit(long percentProfit) {
        this.percentProfit = percentProfit;
    }

    public void setProfit(long profit) {
        this.profit = profit;
    }

    public void setSellMarketOrder(MarketOrder sellMarketOrder) {
        this.sellMarketOrder = sellMarketOrder;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalProfit(long totalProfit) {
        this.totalProfit = totalProfit;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalVolume(long totalVolume) {
        this.totalVolume = totalVolume;
    }
}
