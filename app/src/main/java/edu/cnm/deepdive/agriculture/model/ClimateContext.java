package edu.cnm.deepdive.agriculture.model;

public class ClimateContext {

    public enum DataSource {
        NOAA_LIVE,
        NOT_AVAILABLE
    }

    private final double avgJulyMaxTempF;
    private final double avgLastFrostDoy;
    private final DataSource source;

    public ClimateContext(double avgJulyMaxTempF, double avgLastFrostDoy, DataSource source) {
        this.avgJulyMaxTempF = avgJulyMaxTempF;
        this.avgLastFrostDoy = avgLastFrostDoy;
        this.source = source;
    }

    public static ClimateContext unavailable() {
        return new ClimateContext(0, 0, DataSource.NOT_AVAILABLE);
    }

    public boolean isAvailable()        { return source == DataSource.NOAA_LIVE; }
    public double getAvgJulyMaxTempF()  { return avgJulyMaxTempF; }
    public double getAvgLastFrostDoy()  { return avgLastFrostDoy; }
    public DataSource getSource()       { return source; }
}
