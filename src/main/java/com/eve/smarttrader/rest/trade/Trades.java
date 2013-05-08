package com.eve.smarttrader.rest.trade;

import org.restlet.engine.header.Header;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

public class Trades extends ServerResource {

    @SuppressWarnings("unchecked")
    protected void allowCrossDomainResponse() {
        Series<Header> requestHeaders = (Series<Header>) getRequestAttributes().get("org.restlet.http.headers");
        Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
        requestHeaders.getFirst("Access-Control-Allow-Headers");
        if (responseHeaders == null) {
            responseHeaders = new Series<Header>(Header.class);
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
        }
        responseHeaders.add(new Header("Access-Control-Allow-Origin", "*"));
        responseHeaders.add(new Header("Access-Control-Allow-Headers", requestHeaders.getValuesMap().get("access-control-request-headers")));
    }

}
