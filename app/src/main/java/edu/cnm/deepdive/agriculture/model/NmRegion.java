package edu.cnm.deepdive.agriculture.model;

public enum NmRegion {

    NORTH  ("North NM",   "08313000", "GHCND:USW00023049", 6.8,  3.0, 2.1, 18,  320,  950, 12.0),
    CENTRAL("Central NM", "08330000", "GHCND:USW00023050", 7.4,  8.0, 0.9, 35,  680, 1100,  9.5),
    SOUTH  ("South NM",   "08362500", "GHCND:USW00023044", 7.8, 14.0, 0.6, 55, 1400,  580,  8.0),
    EAST   ("East NM",    "08378500", "GHCND:USW00023009", 7.1,  5.0, 1.4, 42,  820,  140, 11.5),
    WEST   ("West NM",    "09386900", "GHCND:USW00023066", 7.6, 11.0, 0.8, 60, 1100,   55,  7.5);

    public final String displayName;
    public final String usgsSurfaceSiteId;
    public final String noaaStationId;       // TODO: verify before public release — wrong ID = graceful fallback
    public final double defaultPh;
    public final double defaultSar;
    public final double defaultOrganicMatter;
    public final double defaultGwDepth;      // ft — always used; GW toggle deferred to 003-groundwater-toggle
    public final double defaultConductance;  // uS/cm — no live source at surface stations
    public final double defaultStreamflow;   // cfs — offline fallback only
    public final double annualPrecipIn;      // hardcoded NM historical average

    NmRegion(String displayName, String usgsSurfaceSiteId, String noaaStationId,
             double defaultPh, double defaultSar, double defaultOrganicMatter,
             double defaultGwDepth, double defaultConductance,
             double defaultStreamflow, double annualPrecipIn) {
        this.displayName = displayName;
        this.usgsSurfaceSiteId = usgsSurfaceSiteId;
        this.noaaStationId = noaaStationId;
        this.defaultPh = defaultPh;
        this.defaultSar = defaultSar;
        this.defaultOrganicMatter = defaultOrganicMatter;
        this.defaultGwDepth = defaultGwDepth;
        this.defaultConductance = defaultConductance;
        this.defaultStreamflow = defaultStreamflow;
        this.annualPrecipIn = annualPrecipIn;
    }
}
