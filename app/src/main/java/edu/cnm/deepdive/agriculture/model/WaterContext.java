package edu.cnm.deepdive.agriculture.model;

public class WaterContext {

    public enum DataSource {
        USGS_LIVE,
        REGIONAL_DEFAULT,
        OFFLINE_DEFAULT
    }

    private final double avgStreamflow;
    private final DataSource streamflowSource;
    private final double avgGroundwaterDepth;
    private final DataSource gwSource;
    private final double avgConductance;

    public WaterContext(double avgStreamflow, DataSource streamflowSource,
                        double avgGroundwaterDepth, DataSource gwSource,
                        double avgConductance) {
        this.avgStreamflow = avgStreamflow;
        this.streamflowSource = streamflowSource;
        this.avgGroundwaterDepth = avgGroundwaterDepth;
        this.gwSource = gwSource;
        this.avgConductance = avgConductance;
    }

    public static WaterContext fromRegionDefaults(NmRegion region) {
        return new WaterContext(
                region.defaultStreamflow, DataSource.OFFLINE_DEFAULT,
                region.defaultGwDepth,   DataSource.REGIONAL_DEFAULT,
                region.defaultConductance
        );
    }

    public double     getAvgStreamflow()      { return avgStreamflow; }
    public DataSource getStreamflowSource()   { return streamflowSource; }
    public double     getAvgGroundwaterDepth(){ return avgGroundwaterDepth; }
    public DataSource getGwSource()           { return gwSource; }
    public double     getAvgConductance()     { return avgConductance; }
}
