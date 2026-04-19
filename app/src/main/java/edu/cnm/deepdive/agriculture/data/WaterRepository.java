package edu.cnm.deepdive.agriculture.data;

public interface WaterRepository {

    /**
     * Returns the mean daily streamflow (cfs) over the last {@code days} days
     * for the given USGS surface gauge site ID, or null on any failure.
     */
    Double fetchStreamflowAvg(String siteId, int days);

    /**
     * Returns the mean groundwater depth (ft below surface) over the last
     * {@code days} days for the given USGS GW well site ID, or null on failure.
     * TODO: implement live fetch in branch 003-groundwater-toggle
     */
    Double fetchGwDepthAvg(String wellId, int days);
}
