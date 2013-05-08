package com.eve.smarttrader.rest.trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Options;

import com.beimin.eveapi.core.ApiException;
import com.eve.smarttrader.rest.HibernateSessionFactory;
import com.eve.smarttrader.rest.Utils.Constants;
import com.eve.smarttrader.rest.Utils.Utils;
import com.eve.smarttrader.rest.comparator.StationTradeComparatorByProfit;
import com.eve.smarttrader.rest.enums.Region;
import com.eve.smarttrader.rest.enums.SolarSystem;
import com.eve.smarttrader.rest.enums.Station;
import com.eve.smarttrader.rest.models.jpa.MarketData;
import com.eve.smarttrader.rest.models.jpa.MarketOrder;
import com.eve.smarttrader.rest.models.jpa.User;
import com.eve.smarttrader.rest.models.json.StationTrade;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class StationTrades extends Trades {

    private final Session session = HibernateSessionFactory.getSession();

    private User user;

    private Station station;

    private StationTrade createStationTrade(MarketData marketData, MarketOrder cheapestSellMarketOrder, MarketOrder highestBuyMarketOrder) {
        double profit = cheapestSellMarketOrder.getPrice() - highestBuyMarketOrder.getPrice();
        StationTrade stationTrade = new StationTrade();
        stationTrade.setTypeID(marketData.getItemTypeId());
        stationTrade.setStation(station);
        stationTrade.setPercentProfit(Constants.getPercent((long) profit, highestBuyMarketOrder.getPrice()));
        stationTrade.setProfit((long) profit);
        stationTrade.setSellerHoursOld(Hours.hoursBetween(Utils.timeStampToDateTimeUTC(marketData.getGeneratedAt()), DateTime.now()).getHours());
        stationTrade.setName(Constants.getItems().get(marketData.getItemTypeId()).getTypeName());
        stationTrade.setGroupName(Constants.getMarketGroupName().get(Constants.getItems().get(marketData.getItemTypeId()).getMarketGroupId()));
        stationTrade.setBuyPrice(highestBuyMarketOrder.getPrice());
        if (user.getBuyOrderItems().contains(stationTrade.getTypeID())) {
            stationTrade.setUser(user.getName());
        }

        return stationTrade;
    }

    @Get
    public Representation stationTradesByStation() {
        long startTime = System.currentTimeMillis();
        station = Station.fromLong(Long.parseLong(getRequest().getAttributes().get("station").toString()));
        if (station == null) {
            return new StringRepresentation("{}", MediaType.APPLICATION_JSON);
        }
        SolarSystem solarSystem = SolarSystem.getSolarSystemWithStation(station);
        Region region = Region.getRegionWithSolarSystem(solarSystem);
        ImmutableSet<Long> sellableItemsByRegion = Utils.getSellableItemsByRegion(session, region);
        Map<Region, Map<Long, MarketData>> marketDatas = Utils.readBySellableItems(session, sellableItemsByRegion, new String[] { region.toString() });
        List<StationTrade> stationTrades = new ArrayList<StationTrade>();
        user = Utils.getUserByName(session, "Leolie Aylet");
        try {
            Utils.setUserItems(user);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        for (MarketData marketData : marketDatas.get(region).values()) {
            if (!Constants.getItems().containsKey(marketData.getItemTypeId())) {
                continue;
            }
            MarketOrder cheapestSellMarketOrder = Utils.getCheapestSellMarketOrder(marketData.getMarketOrders());
            MarketOrder highestBuyMarketOrder = Utils.getHighestBuyMarketOrder(marketData.getMarketOrders());
            if (cheapestSellMarketOrder != null && highestBuyMarketOrder != null) {
                StationTrade stationHubTrade = createStationTrade(marketData, cheapestSellMarketOrder, highestBuyMarketOrder);
                if (stationHubTrade.getPercentProfit() > Constants.getMinPercentStationTrading()) {
                    stationTrades.add(stationHubTrade);
                }
            }
        }
        Collections.sort(stationTrades, new StationTradeComparatorByProfit());
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject response = new JsonObject();
        response.add("trades", gson.toJsonTree(stationTrades));
        response.addProperty("time", (System.currentTimeMillis() - startTime));
        StringRepresentation result = new StringRepresentation(response.toString(), MediaType.APPLICATION_JSON);
        allowCrossDomainResponse();

        return result;
    }

    @Options
    public String status() {
        allowCrossDomainResponse();

        return "OK !";
    }

}
