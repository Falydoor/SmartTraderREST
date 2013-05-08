package com.eve.smarttrader.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.eve.smarttrader.rest.Utils.Constants;
import com.eve.smarttrader.rest.Utils.Utils;
import com.eve.smarttrader.rest.trade.HubTrades;
import com.eve.smarttrader.rest.trade.PenuryTrades;
import com.eve.smarttrader.rest.trade.StationTrades;

public class SmartTraderRest extends Application {

    @Override
    public Restlet createInboundRoot() {

        Router router = new Router(getContext());
        router.attach("/hubtrades/{station}", HubTrades.class);
        router.attach("/penurytrades/{station}", PenuryTrades.class);
        router.attach("/stationtrades/{station}", StationTrades.class);

        return router;
    }

    @Override
    public synchronized void start() throws Exception {
        super.start();
        Constants.setItems(Utils.loadItems());
        Constants.setMarketGroupName(Utils.loadMarketGroupsName());
    }

}
