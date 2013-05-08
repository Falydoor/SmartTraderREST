package com.eve.smarttrader.rest.enums;

public enum Station {
    AmarrHUB(60008494L), DodixieHUB(60011866L), HekHUB(60005686L), JitaHUB(60003760L), RensHUB(60004588L), SobasekiHUB(60003916L);

    public static Station fromLong(long stationId) {
        for (Station station : values()) {
            if (stationId == station.getId()) {
                return station;
            }
        }
        return null;
    }

    public static Station getStationWithRegion(Region region) {
        if (region == Region.DOMAIN) {
            return AmarrHUB;
        }
        if (region == Region.HEIMATAR) {
            return RensHUB;
        }
        if (region == Region.METROPOLIS) {
            return HekHUB;
        }
        if (region == Region.SINQ_LAISON) {
            return DodixieHUB;
        }
        if (region == Region.LONETREK) {
            return SobasekiHUB;
        }
        if (region == Region.THE_FORGE) {
            return JitaHUB;
        }
        return null;
    }

    public static Station getStationWithSolarSystem(SolarSystem solarSystem) {
        if (solarSystem == SolarSystem.Amarr) {
            return AmarrHUB;
        }
        if (solarSystem == SolarSystem.Rens) {
            return RensHUB;
        }
        if (solarSystem == SolarSystem.Hek) {
            return HekHUB;
        }
        if (solarSystem == SolarSystem.Dodixie) {
            return DodixieHUB;
        }
        if (solarSystem == SolarSystem.Sobaseki) {
            return SobasekiHUB;
        }
        if (solarSystem == SolarSystem.Jita) {
            return JitaHUB;
        }
        return null;
    }

    private final long stationId;

    private Station(long stationId) {
        this.stationId = stationId;
    }

    long getId() {
        return this.stationId;
    }
}
