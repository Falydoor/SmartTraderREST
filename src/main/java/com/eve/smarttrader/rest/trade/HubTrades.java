package com.eve.smarttrader.rest.trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Options;

import com.eve.smarttrader.rest.HibernateSessionFactory;
import com.eve.smarttrader.rest.Utils.Constants;
import com.eve.smarttrader.rest.Utils.Utils;
import com.eve.smarttrader.rest.comparator.HubTradeComparatorByProfitPercent;
import com.eve.smarttrader.rest.enums.Region;
import com.eve.smarttrader.rest.enums.SolarSystem;
import com.eve.smarttrader.rest.enums.Station;
import com.eve.smarttrader.rest.models.jpa.MarketData;
import com.eve.smarttrader.rest.models.jpa.MarketOrder;
import com.eve.smarttrader.rest.models.json.HubTrade;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class HubTrades extends Trades {

    private final Session session = HibernateSessionFactory.getSession();

    private void createTrade(MarketData buyerMD, MarketOrder sellerMO, Station station, List<HubTrade> hubTrades) {
        DescriptiveStatistics meanPrice = new DescriptiveStatistics();
        HubTrade hubTrade = new HubTrade();
        float thresholdPrice = 0;
        for (MarketOrder order : buyerMD.getMarketOrders()) {
            if (order.isBid() || order.getPrice() >= sellerMO.getPrice() || order.getPrice() >= thresholdPrice && thresholdPrice != 0) {
                break;
            }
            if (thresholdPrice == 0) {
                thresholdPrice = order.getPrice() * 1.1F;
            }
            meanPrice.addValue(order.getPrice());
            hubTrade.addTotalPrice(order.getPrice() * order.getVolRemaining());
            hubTrade.addTotalQuantity(order.getVolRemaining());
        }
        if (thresholdPrice != 0) {
            hubTrade.setAveragePrice((long) meanPrice.getMean());
            hubTrade.setProfit(sellerMO.getPrice() - hubTrade.getAveragePrice());
            hubTrade.setTotalProfit(hubTrade.getProfit() * hubTrade.getTotalQuantity());
            hubTrade.setPercentProfit(Constants.getPercent(hubTrade.getTotalProfit(), hubTrade.getTotalPrice()));
            if (hubTrade.getPercentProfit() > 0) {
                hubTrade.setTypeID(buyerMD.getItemTypeId());
                hubTrade.setSellMarketOrder(sellerMO);
                hubTrade.setStation(station);
                hubTrade.setTotalVolume((long) (Constants.getItems().get(buyerMD.getItemTypeId()).getVolume() * hubTrade.getTotalQuantity()));
                hubTrade.setSellerHoursOld(Hours.hoursBetween(Utils.timeStampToDateTimeUTC(sellerMO.getMarketData().getGeneratedAt()), DateTime.now()).getHours());
                hubTrade.setBuyerHoursOld(Hours.hoursBetween(Utils.timeStampToDateTimeUTC(buyerMD.getGeneratedAt()), DateTime.now()).getHours());
                hubTrade.setName(Constants.getItems().get(buyerMD.getItemTypeId()).getTypeName());
                hubTrade.setGroupName(Constants.getMarketGroupName().get(Constants.getItems().get(buyerMD.getItemTypeId()).getMarketGroupId()));

                hubTrades.add(hubTrade);
            }
        }
    }

    private List<HubTrade> getHubTrades(Map<Region, Map<Long, MarketData>> marketDatas, List<Region> regionBuyers, Region regionSeller) {
        List<HubTrade> hubTrades = new ArrayList<HubTrade>();
        Map<Region, Station> stationByRegion = new HashMap<Region, Station>();
        for (Region region : Region.values()) {
            stationByRegion.put(region, Station.getStationWithRegion(region));
        }
        for (MarketData sellerMOs : marketDatas.get(regionSeller).values()) {
            for (Region region : regionBuyers) {
                if (marketDatas.get(region).containsKey(sellerMOs.getItemTypeId())) {
                    MarketData sellerMarketData = marketDatas.get(region).get(sellerMOs.getItemTypeId());
                    MarketOrder cheapestSellMarketOrder = Utils.getCheapestSellMarketOrder(sellerMOs.getMarketOrders());
                    createTrade(sellerMarketData, cheapestSellMarketOrder, stationByRegion.get(region), hubTrades);
                }
            }
        }
        Collections.sort(hubTrades, new HubTradeComparatorByProfitPercent());

        return hubTrades;
    }

    @Get
    public Representation hubTradesByStation() {
        long startTime = System.currentTimeMillis();
        Station station = Station.fromLong(Long.parseLong(getRequest().getAttributes().get("station").toString()));
        if (station == null) {
            return new StringRepresentation("{}", MediaType.APPLICATION_JSON);
        }
        SolarSystem solarSystem = SolarSystem.getSolarSystemWithStation(station);
        Region region = Region.getRegionWithSolarSystem(solarSystem);
        String[] regions = new String[Region.values().length];
        for (int i = 0; i < Region.values().length; ++i) {
            regions[i] = Region.values()[i].toString();
        }
        ImmutableSet<Long> sellableItemsByRegion = Utils.getSellableItemsByRegion(session, region);
        Map<Region, Map<Long, MarketData>> marketDatas = Utils.readBySellableItems(session, sellableItemsByRegion, regions);
        List<HubTrade> hubTrades = getHubTrades(marketDatas, Region.getBuyable(region), region);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject response = new JsonObject();
        response.add("trades", gson.toJsonTree(hubTrades));
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
