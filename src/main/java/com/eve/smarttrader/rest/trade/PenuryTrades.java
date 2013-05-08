package com.eve.smarttrader.rest.trade;

import java.util.ArrayList;
import java.util.List;

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
import com.eve.smarttrader.rest.enums.Region;
import com.eve.smarttrader.rest.enums.SolarSystem;
import com.eve.smarttrader.rest.enums.Station;
import com.eve.smarttrader.rest.models.jpa.MarketData;
import com.eve.smarttrader.rest.models.json.PenuryTrade;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class PenuryTrades extends Trades {

    private final Session session = HibernateSessionFactory.getSession();

    private Station station;

    private PenuryTrade createPenuryTrade(MarketData marketData) {
        PenuryTrade penuryTrade = new PenuryTrade();
        penuryTrade.setTypeID(marketData.getItemTypeId());
        penuryTrade.setSellerHoursOld(Hours.hoursBetween(Utils.timeStampToDateTimeUTC(marketData.getGeneratedAt()), DateTime.now()).getHours());
        penuryTrade.setName(Constants.getItems().get(marketData.getItemTypeId()).getTypeName());
        penuryTrade.setGroupName(Constants.getMarketGroupName().get(Constants.getItems().get(marketData.getItemTypeId()).getMarketGroupId()));
        penuryTrade.setStation(station);

        return penuryTrade;
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
        List<MarketData> penuryItems = Utils.getPenuryItems(session, sellableItemsByRegion, region);
        List<PenuryTrade> penuryHubTrades = new ArrayList<PenuryTrade>();
        for (MarketData marketData : penuryItems) {
            penuryHubTrades.add(createPenuryTrade(marketData));
        }
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonObject response = new JsonObject();
        response.add("trades", gson.toJsonTree(penuryHubTrades));
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
