package edu.cnm.deepdive.agriculture.data;

import edu.cnm.deepdive.agriculture.model.ClimateContext;

public interface ClimateRepository {

    /**
     * Returns a populated ClimateContext for the given NOAA GHCND station ID,
     * or ClimateContext.unavailable() on any failure. Never throws.
     */
    ClimateContext fetch(String noaaStationId);
}
