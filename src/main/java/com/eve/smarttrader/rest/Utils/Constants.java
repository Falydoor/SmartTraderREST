package com.eve.smarttrader.rest.Utils;

import java.util.Map;

import com.eve.smarttrader.rest.models.jpa.Item;
import com.google.common.collect.ImmutableMap;

public final class Constants {

    private static final int HUNDRED = 100;

    private static Map<Long, Item> items;

    private static ImmutableMap<Integer, String> marketGroupName;

    private static final int MINPERCENTSTATIONTRADING = 9;

    public static Map<Long, Item> getItems() {
        return items;
    }

    public static ImmutableMap<Integer, String> getMarketGroupName() {
        return marketGroupName;
    }

    public static int getMinPercentStationTrading() {
        return MINPERCENTSTATIONTRADING;
    }

    public static long getPercent(long firstNumber, long secondNumber) {
        return HUNDRED * firstNumber / secondNumber;
    }

    public static void setItems(Map<Long, Item> items) {
        Constants.items = items;
    }

    public static void setMarketGroupName(ImmutableMap<Integer, String> marketGroupName) {
        Constants.marketGroupName = marketGroupName;
    }

    private Constants() {
    }

}
