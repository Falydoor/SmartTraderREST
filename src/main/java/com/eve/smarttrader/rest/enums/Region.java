package com.eve.smarttrader.rest.enums;

import java.util.ArrayList;
import java.util.List;

public enum Region {
    DOMAIN(10000043L), HEIMATAR(10000030L), LONETREK(10000016L), METROPOLIS(10000042L), SINQ_LAISON(10000032L), THE_FORGE(10000002L);

    public static Region fromLong(long regionId) {
        for (Region region : values()) {
            if (regionId == region.getId()) {
                return region;
            }
        }
        return null;
    }

    public static List<Region> getBuyable(Region sellRegion) {
        List<Region> regions = new ArrayList<Region>();
        for (Region region : values()) {
            if (region != sellRegion) {
                regions.add(region);
            }
        }
        return regions;
    }

    public static Region getRegionWithSolarSystem(SolarSystem solarSystem) {
        if (solarSystem == SolarSystem.Amarr) {
            return DOMAIN;
        }
        if (solarSystem == SolarSystem.Dodixie) {
            return SINQ_LAISON;
        }
        if (solarSystem == SolarSystem.Hek) {
            return METROPOLIS;
        }
        if (solarSystem == SolarSystem.Jita) {
            return THE_FORGE;
        }
        if (solarSystem == SolarSystem.Rens) {
            return HEIMATAR;
        }
        if (solarSystem == SolarSystem.Sobaseki) {
            return LONETREK;
        }
        return null;
    }

    private final long regionId;

    private Region(long regionId) {
        this.regionId = regionId;
    }

    public long getId() {
        return this.regionId;
    }
}
