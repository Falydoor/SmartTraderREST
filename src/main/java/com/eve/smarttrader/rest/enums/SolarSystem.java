package com.eve.smarttrader.rest.enums;

public enum SolarSystem {
    Amarr(30002187L), Dodixie(30002659L), Hek(30002053L), Jita(30000142L), Rens(30002510L), Sobaseki(30001363L);

    public static SolarSystem fromLong(long solarSystemId) {
        for (SolarSystem solarSystem : values()) {
            if (solarSystemId == solarSystem.getId()) {
                return solarSystem;
            }
        }
        return null;
    }

    public static SolarSystem getSolarSystemWithRegion(Region region) {
        if (region == Region.DOMAIN) {
            return Amarr;
        }
        if (region == Region.HEIMATAR) {
            return Rens;
        }
        if (region == Region.METROPOLIS) {
            return Hek;
        }
        if (region == Region.SINQ_LAISON) {
            return Dodixie;
        }
        if (region == Region.LONETREK) {
            return Sobaseki;
        }
        if (region == Region.THE_FORGE) {
            return Jita;
        }
        return null;
    }

    public static SolarSystem getSolarSystemWithStation(Station station) {
        if (station == Station.AmarrHUB) {
            return Amarr;
        }
        if (station == Station.RensHUB) {
            return Rens;
        }
        if (station == Station.HekHUB) {
            return Hek;
        }
        if (station == Station.DodixieHUB) {
            return Dodixie;
        }
        if (station == Station.SobasekiHUB) {
            return Sobaseki;
        }
        if (station == Station.JitaHUB) {
            return Jita;
        }
        return null;
    }

    private final long solarSystemId;

    private SolarSystem(long solarSystemId) {
        this.solarSystemId = solarSystemId;
    }

    public long getId() {
        return this.solarSystemId;
    }
}
