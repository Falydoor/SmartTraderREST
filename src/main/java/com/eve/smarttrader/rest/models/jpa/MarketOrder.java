package com.eve.smarttrader.rest.models.jpa;

// Generated 28 avr. 2013 21:59:35 by Hibernate Tools 4.0.0

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * MarketOrder generated by hbm2java
 */
@Entity
@Table(name = "marketorder", catalog = "eve")
public class MarketOrder implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private long orderId;
    private MarketData marketData;
    private boolean bid;
    private long duration;
    private Date issueDate;
    private long minVolume;
    private long price;
    private String solarSystem;
    private String station;
    private long volEntered;
    private long volRemaining;

    public MarketOrder() {
    }

    public MarketOrder(long orderId, boolean bid, long duration, long minVolume, long price, long volEntered, long volRemaining) {
        this.orderId = orderId;
        this.bid = bid;
        this.duration = duration;
        this.minVolume = minVolume;
        this.price = price;
        this.volEntered = volEntered;
        this.volRemaining = volRemaining;
    }

    public MarketOrder(long orderId, MarketData marketData, boolean bid, long duration, Date issueDate, long minVolume, long price, String solarSystem, String station, long volEntered, long volRemaining) {
        this.orderId = orderId;
        this.marketData = marketData;
        this.bid = bid;
        this.duration = duration;
        this.issueDate = issueDate;
        this.minVolume = minVolume;
        this.price = price;
        this.solarSystem = solarSystem;
        this.station = station;
        this.volEntered = volEntered;
        this.volRemaining = volRemaining;
    }

    @Id
    @Column(name = "orderID", unique = true, nullable = false)
    public long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketData_id")
    public MarketData getMarketData() {
        return this.marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }

    @Column(name = "bid", nullable = false)
    public boolean isBid() {
        return this.bid;
    }

    public void setBid(boolean bid) {
        this.bid = bid;
    }

    @Column(name = "duration", nullable = false)
    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issueDate", length = 19)
    public Date getIssueDate() {
        return this.issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name = "minVolume", nullable = false)
    public long getMinVolume() {
        return this.minVolume;
    }

    public void setMinVolume(long minVolume) {
        this.minVolume = minVolume;
    }

    @Column(name = "price", nullable = false)
    public long getPrice() {
        return this.price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Column(name = "solarSystem")
    public String getSolarSystem() {
        return this.solarSystem;
    }

    public void setSolarSystem(String solarSystem) {
        this.solarSystem = solarSystem;
    }

    @Column(name = "station")
    public String getStation() {
        return this.station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Column(name = "volEntered", nullable = false)
    public long getVolEntered() {
        return this.volEntered;
    }

    public void setVolEntered(long volEntered) {
        this.volEntered = volEntered;
    }

    @Column(name = "volRemaining", nullable = false)
    public long getVolRemaining() {
        return this.volRemaining;
    }

    public void setVolRemaining(long volRemaining) {
        this.volRemaining = volRemaining;
    }

}