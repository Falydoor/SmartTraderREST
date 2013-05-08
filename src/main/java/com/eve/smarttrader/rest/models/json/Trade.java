package com.eve.smarttrader.rest.models.json;

import com.eve.smarttrader.rest.enums.Station;
import com.google.gson.annotations.Expose;

public abstract class Trade {

    @Expose
    protected String groupName;

    @Expose
    protected String name;

    @Expose
    protected int sellerHoursOld;

    @Expose
    protected Station station;

    @Expose
    protected long typeID;
    
    @Expose
    protected String user;

    public String getGroupName() {
        return groupName;
    }

    public String getName() {
        return name;
    }

    public int getSellerHoursOld() {
        return sellerHoursOld;
    }

    public Station getStation() {
        return station;
    }

    public long getTypeID() {
        return typeID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSellerHoursOld(int sellerHoursOld) {
        this.sellerHoursOld = sellerHoursOld;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
}
