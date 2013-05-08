package com.eve.smarttrader.rest.models.jpa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user", catalog = "eve")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private long charId;

    private int keyId;

    private String vCode;
    
    @Transient
    private Set<Long> buyOrderItems = new HashSet<>();
    
    @Transient
    private Set<Long> sellOrderItems = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCharId() {
        return charId;
    }

    public void setCharId(long charId) {
        this.charId = charId;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getvCode() {
        return vCode;
    }

    public void setvCode(String vCode) {
        this.vCode = vCode;
    }

    public Set<Long> getBuyOrderItems() {
        return buyOrderItems;
    }

    public void setBuyOrderItems(Set<Long> buyOrderItems) {
        this.buyOrderItems = buyOrderItems;
    }

    public Set<Long> getSellOrderItems() {
        return sellOrderItems;
    }

    public void setSellOrderItems(Set<Long> sellOrderItems) {
        this.sellOrderItems = sellOrderItems;
    }

}
